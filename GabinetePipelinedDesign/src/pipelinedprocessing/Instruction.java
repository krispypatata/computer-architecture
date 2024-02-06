// package declaration
package pipelinedprocessing;

// import statement(s)
import java.util.ArrayList;

// class definition
public class Instruction {
	// *******************************************************************************************
	// attribute(s)
	private String operation;
	private String destinationRegister;
	private String sourceOperand1;
	private String sourceOperand2;
	private String instruction;

	private ArrayList<String> pipelinedVersion;

	// constant(s)
	public final static String FETCH = "F";
	public final static String DECODE = "D";
	public final static String EXECUTE = "E";
	public final static String MEMORY = "M";
	public final static String WRITEBACK = "W";
	public final static String STALL = "S";
	public final static String NONE = "-";

	// regular expressions
	public final static String ALLOWED_REGISTERS = "^(r[0-9]|r1[0-5])$";
	public final static String ALLOWED_OPERATIONS = "^(add|sub|div|mul)$";

	// *******************************************************************************************
	// constructor(s)
	Instruction (String opCode, String operand1, String operand2, String operand3) {
		// initializing attribute(s) data
		this.operation = opCode.toLowerCase();
		this.destinationRegister = operand1.toUpperCase();
		this.sourceOperand1 = operand2.toUpperCase();
		this.sourceOperand2 = operand3.toUpperCase();

		this.pipelinedVersion = new ArrayList<String>();

		this.instruction = this.operation + " " + this.destinationRegister + ", " + this.sourceOperand1 + ", " + this.sourceOperand2;
	}


	// *******************************************************************************************
	// method(s)

	// ===========================================================================================
	void viewInstruction() {
		System.out.println(this.getInstruction());
	}


	// ===========================================================================================
	/*
	 * 		Getter(s)
	 */
	public String getOperation() {
		return this.operation;
	}

	public String getDestinationRegister() {
		return this.destinationRegister;
	}


	public String getSourceOperand1() {
		return this.sourceOperand1;
	}


	public String getSourceOperand2() {
		return this.sourceOperand2;
	}

	public ArrayList<String> getPipelinedVersion() {
		return this.pipelinedVersion;
	}

	public String getInstruction () {
		return this.instruction;
	}

}
