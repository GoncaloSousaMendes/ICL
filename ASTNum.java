public class ASTNum implements ASTNode {

	int val;

	public IValue eval(Environ<IValue> env) 
	{ 
		return new IntegerValue(val); 
	}
	
	public Type typeCheck(Environ<Type> env) throws TypeErrorException{
		return IntType.value;
	}

    public ASTNum(int n)
    {
        	val = n;
     }
        
    @Override
    public String toString(){
    	return Integer.toString(val);
    }

	
	public void compile(CodeBlock code, CompilerFrame env)throws UndeclaredIdentifierException, DuplicateIdentifierException  {
		code.emit_push(val);
	}

}

