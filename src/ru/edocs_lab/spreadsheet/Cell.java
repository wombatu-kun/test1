package ru.edocs_lab.spreadsheet;

enum Type {TEXT, NUMBER, ERROR, LINK, NULL, X3};
enum ErrMsg {GARBAGE, DIVBY0, LOOP, CYCLE, OVERFLOW, NULLOPRND, TEXTOPRND, OUTOFRNG};

public abstract class Cell {
	protected static final String SHLAK_PTRN = "(^=.*[^-+/*A-Z0-9]+.*)|(.*[^0-9]$)|(^[^=1-9].*)|(.*[0-9][A-Z].*)|(.*[A-Z][-*/+].*)|(.*[A-Z][A-Z]+.*)|(.*[-*/+][-*/+]+.*)|(.*[^1-9]0[0-9]+.*)|(^=[-*/+].*)|(.*[A-Z]0.*)";
	//непустую не-' строку будем считать шлаком, если: есть посторонние символы; заканчиваетс€ не на цифру;
	//начинаетс€ не с = или с цифры; есть цифра перед буквой; есть операци€ после буквы; две и больше буквы р€дом;
	//два и больше действи€ р€дом; р€д цифр, начинающийс€ с нул€; начинаетс€ с =операци€; €чейка с индексом 0;
	
	private String mLabel;
	private Type mType;
	private String mIn;
	private String mOut;	
	private boolean mIsProcessed;
	
	protected Cell(String label, String inStr) {
		setLabel(label);
		setInput(inStr);
		setProcessed(false);
		setType(Type.X3);
		setOutput("");
	}

	protected abstract void evaluateLocal();
	public abstract void evaluate();
	
	protected String getInput() {
		return mIn;
	}

	protected void setInput(String in) {
		mIn = in;
	}

	protected void setOutput(String out) {
		mOut = out;
	}

	protected String getLabel() {
		return mLabel;
	}

	protected void setLabel(String label) {
		mLabel = label;
	}

	protected void setProcessed(boolean isProcessed) {
		mIsProcessed = isProcessed;
	}

	protected void setType(Type type) {
		mType = type;
	}
	
	protected void setError(ErrMsg e) {
		setType(Type.ERROR);
		setOutput("#" + e.toString());
		setProcessed(true);
	}
	
	public boolean isProcessed() {
		return mIsProcessed;
	}
	
	public Type getType() {
		return mType;
	}
	
	public String getOutput() {
		return mOut;
	}
	
	@Override
	public String toString() {
		return getOutput();
	}
}
