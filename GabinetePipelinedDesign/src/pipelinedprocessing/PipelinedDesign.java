// package declaration
package pipelinedprocessing;

// import statement(s)
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

// class definition
public class PipelinedDesign {
	// *******************************************************************************************
	// attribute(s)
	private ArrayList<Instruction> instructionSet;
	private ArrayList<Instruction> pipelinedInstructionSet;


	// *******************************************************************************************
	// constructor(s)
	public PipelinedDesign() {
		// initialize attribute(s)
		this.instructionSet = new ArrayList<Instruction>();
		this.pipelinedInstructionSet = new ArrayList<Instruction>();

		// call the method of reading inputs
		this.readInputFile();

	}


	// *******************************************************************************************
	// method(s)
	// ===========================================================================================
	/*
	 *		get instructions from input.txt
	 */
	private void readInputFile () {
		// locate the input.txt file
		// use file separator(s) to ensure that the code works correctly on different operating systems
		String projectDirectory = System.getProperty("user.dir");
		String packageDirectory = "src" + File.separator + "pipelinedprocessing";
		String fileName = "input.txt";
		File inputFile = new File(projectDirectory, packageDirectory + File.separator + fileName);


		String fileContent = "";				// will contain the string contents of the input file

		// read the contents of the input.txt file
		try {
			Scanner scanner = new Scanner(inputFile);

			int instructionLinesCounter = 0;
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				// read lines that are not empty
                if (!line.isEmpty()) {
    				fileContent += line;
    				fileContent += (" \n");
    				instructionLinesCounter += 1; 		// update the counter for instruction lines
                }
			}

			// close the scanner
			scanner.close();

            // check if the input file is empty
			// OR if the instruction lines within the input text file already exceeds the preferred limit (of 20 lines)
            if (instructionLinesCounter>20 || fileContent.isEmpty()) {
            	if (fileContent.isEmpty()) {
                	System.out.println("\n----------------------------------------------------------------------------------");
                	System.out.println("ERROR:\tThe input file is empty!");
                	System.out.println("----------------------------------------------------------------------------------");
            	} else {
                	System.out.println("\n----------------------------------------------------------------------------------");
                	System.out.println("ERROR:\tThe input file has reached its maximum allowed number of instruction lines!");
                	System.out.println("\tEnsure that the number of instruciton lines in the input file does not exceed 20.");
                	System.out.println("----------------------------------------------------------------------------------");
            	}

            	this.exit();
            }

		} catch (FileNotFoundException e) {
			// catch block
			e.printStackTrace();
		}

		// for checking
//		System.out.println(fileContent);

		// correctly format the data from the input file's string contents
		this.formatInputContents(fileContent);
	}


	// ===========================================================================================
	/*
	 *		format the instructions extracted from the input file
	 *		store the extracted data in appropriate storage variables
	 */
	private void formatInputContents(String fileContent) {
		// for checking
//		System.out.println(fileContent);

		// extract the lines from the input file's string contents
		String[] lines = fileContent.split("\n");

		for (String line : lines) {
	        // replace commas in each line with whitespaces
	        String lineWithoutCommas = line.replaceAll(",", " ");

	        // split the recently formatted lines with whitespaces
	        String[] formattedLine = lineWithoutCommas.split("\\s+");

	        // check if the final formatted line has been split properly (should have exactly 4 elements - opcode, op1, op2, op3)
	        // if not then prompt an error message about reading the input instructions
	        if (formattedLine.length != 4) {
            	System.out.println("\n----------------------------------------------------------------------------------");
            	System.out.println("ERROR:\tCannot extract instructions provided in the input file correctly!");
            	System.out.println("\tEnsure that each line in the input file follows the correct format.");
            	System.out.println("\tInstruction format:");
            	System.out.println("\t\topcode operand1, operand2, operand3");
            	System.out.println("----------------------------------------------------------------------------------");

            	this.exit();
	        }

	        /*
	         * 		check if the opcode and the operands in the given instruction follow the set of constraints specified in the instruction set design
	         */
	        // a. check if only the arithmetic operations "add", "sub", "mul", "div" are used
	        if (!formattedLine[0].toLowerCase().matches(Instruction.ALLOWED_OPERATIONS)) {
            	System.out.println("\n----------------------------------------------------------------------------------");
            	System.out.println("ERROR:\tCannot distinguish the opcode used for the instruction!");
            	System.out.println("\tEnsure that only the arithmetic operations - \"add\", \"sub\", \"mul\", and \"div\" ");
            	System.out.println("\t- are used for the opcode.");
            	System.out.println("----------------------------------------------------------------------------------");

            	this.exit();
	        }


	        // b. check if only R0-R15 registers are used
	        for (int operand=1; operand<3; operand++) {
		        if (formattedLine[operand].toLowerCase().matches(Instruction.ALLOWED_REGISTERS) == false) {
	            	System.out.println("\n----------------------------------------------------------------------------------");
	            	System.out.println("ERROR:\tCannot distinguish the operand register(s) used for the instruction!");
	            	System.out.println("\tEnsure that only the registers from R0 to R15 are used for the operands.");
	            	System.out.println("----------------------------------------------------------------------------------");

	            	this.exit();
		        }
	        }

	        // if the instruction passes all the given constraints
	        // instantiate a new instruction object with the extracted data from the formatted line
	        Instruction instruction = new Instruction(formattedLine[0], formattedLine[1], formattedLine[2], formattedLine[3]);

	        // add created instruction object into our ArrayList of instructions
	        this.instructionSet.add(instruction);
		} // end of for loop

		// get the pipelined version of the instruction set
		this.performPipelinedProcessing();
	}


	// ===========================================================================================
	/*
	 *		create a pipelined design for our instruction set
	 */
	private void performPipelinedProcessing() {
		// loop through the instructions stored in the instruction set
		for (Instruction instruction : this.instructionSet) {

			// check if the time diagram is empty
			if (this.pipelinedInstructionSet.isEmpty()) {
				instruction.getPipelinedVersion().add(Instruction.FETCH);
				instruction.getPipelinedVersion().add(Instruction.DECODE);
				instruction.getPipelinedVersion().add(Instruction.EXECUTE);
				instruction.getPipelinedVersion().add(Instruction.MEMORY);
				instruction.getPipelinedVersion().add(Instruction.WRITEBACK);

				this.pipelinedInstructionSet.add(instruction);
				continue;
			} else {
				// permits the instruction to enter the first stage (FETCH) after the previous instruction has made use of it
				for (int i=0; i<this.pipelinedInstructionSet.size(); i++)
					instruction.getPipelinedVersion().add(Instruction.NONE);
				instruction.getPipelinedVersion().add(Instruction.FETCH);

				// traverse through the pipelined instructions and check for data hazards
				// create a variable that indicates how many time units the current instruction needs to stall until another instruction (with which it shares a hazard) finishes
				int stallTime = 0;

				for (Instruction pipelinedInstruction : this.pipelinedInstructionSet) {
					boolean hasDataHazard = false;

					// read after write (RAW)
					if (instruction.getSourceOperand1().toLowerCase()
							.equals(pipelinedInstruction.getDestinationRegister().toLowerCase())
						|| instruction.getSourceOperand2().toLowerCase()
							.equals(pipelinedInstruction.getDestinationRegister().toLowerCase()) )
							hasDataHazard = true;

					// write after write (WAW)
					if (instruction.getDestinationRegister().toLowerCase()
							.equals(pipelinedInstruction.getDestinationRegister().toLowerCase()) )
							hasDataHazard = true;

					// write after read (WAR)
					if (instruction.getDestinationRegister().toLowerCase()
							.equals(pipelinedInstruction.getSourceOperand1().toLowerCase())
						|| instruction.getDestinationRegister().toLowerCase()
							.equals(pipelinedInstruction.getSourceOperand2().toLowerCase()) )
							hasDataHazard = true;

					// update the stallTime's value if a data hazard has been encountered
					if (hasDataHazard) stallTime = pipelinedInstruction.getPipelinedVersion().size() - instruction.getPipelinedVersion().size();


				}

				// just add "S" to the instruction's pipelined version
				for (int i=0; i<stallTime; i++)
					instruction.getPipelinedVersion().add(Instruction.STALL);

				// proceed to the rest of the stages
				this.checkIfStageIsOccupied(instruction, Instruction.DECODE);
				this.checkIfStageIsOccupied(instruction, Instruction.EXECUTE);
				this.checkIfStageIsOccupied(instruction, Instruction.MEMORY);
				this.checkIfStageIsOccupied(instruction, Instruction.WRITEBACK);

				// add the instruction to the list of checked instructions (pipelined instructions)
				this.pipelinedInstructionSet.add(instruction);

			} // end of if-else
		} // end of for loop

		// output the results (time diagram)
		String timeDiagram = ""; // string variable if we want to export the output
		for (Instruction pipelinedInstruction: this.pipelinedInstructionSet) {
			timeDiagram += pipelinedInstruction.getInstruction() + "\t";
			for (String pipelinedString: pipelinedInstruction.getPipelinedVersion()) {
				timeDiagram += pipelinedString + " ";
			}
			timeDiagram += "\n";
		}
		timeDiagram += "<end>";

		// printing the output in the console
		System.out.println(timeDiagram);

	}


	// ===========================================================================================
	/*
	 *		check if no other instruction is using a particular stage at a particular time unit
	 */
	private void checkIfStageIsOccupied (Instruction instruction, String stage) {
		int timeUnit;

		/*
		 * 	since instructions have to be performed  in order (e.g., instruction i+1 cannot finish earlier than instruction i)
		 * 	check first if the previous instruction already finished executing the given stage before allowing the
		 * 	... current instruction to enter the said stage
		 * */
		// get the previous instruction
		Instruction previousInstruction = this.pipelinedInstructionSet.get(this.pipelinedInstructionSet.size()-1);

		// stall until the specified stage is free
		for (int index=1; previousInstruction.getPipelinedVersion().get(index-1) != stage; index ++)
			if ( instruction.getPipelinedVersion().size() < index) instruction.getPipelinedVersion().add(Instruction.STALL);


		// a flag for checking if a certain stage is free in a given time unit
		// initialized to true since we needed it for a loop
		boolean stageIsOccupied = true;

		while (stageIsOccupied) {
			stageIsOccupied = false; 	// reset the flag value to false

			// check if some instruction(s) is/are using the specified stage at a given time unit
			timeUnit = instruction.getPipelinedVersion().size();
			for (Instruction pipelinedInstruction: this.pipelinedInstructionSet) {
				// to avoid null pointer exception (when an instruction is already finished running, but we're still trying to get some data out of it)
				if (pipelinedInstruction.getPipelinedVersion().size() <= timeUnit)
					continue;

				// update the flag if a certain instruction currently occupies the specified stage
				if (pipelinedInstruction.getPipelinedVersion().get(timeUnit)
						.equals(stage) )
					stageIsOccupied = true;
			}

			// just add "S" to the pipelined version of the given instruction if the specified stage is still occupied
			if (stageIsOccupied) instruction.getPipelinedVersion().add(Instruction.STALL);
			// else add the specified stage to the pioelined version of the instruction and exit the loop (since stageIsOccupied remained false)
			else instruction.getPipelinedVersion().add(stage);
		} // end of while loop
	}


	// ===========================================================================================
	/*
	 *		terminate the program
	 */
	private void exit() {
    	System.out.println("\n==================================================================================");
    	System.out.println("\t\t\tTerminating the program... Bye!");
    	System.out.println("==================================================================================");

    	System.exit(0);
	}

}
