package AST;

import parser.*;
import Types.*;
import exceptions.*;
import Values.*;
import main.*;

public class ASTBool implements ASTNode {

	boolean val;

	public IValue eval(Environ<IValue> env) throws ExecutionErrorException 
	{ 
		return new BooleanValue(val); 
	}
	
	public Type typeCheck(Environ<Type> env) throws TypeErrorException, DuplicateIdentifierException, UndeclaredIdentifierException{
		return BoolType.value;
	}

    public ASTBool(boolean n)
    {
        	val = n;
     }
        
    @Override
    public String toString(){
    	return Boolean.toString(val);
    }

	
	public void compile(CodeBlock code, CompilerFrame env)throws UndeclaredIdentifierException, DuplicateIdentifierException  {
		if(val) code.emit_push(1);
		else code.emit_push(0);
	}

}
