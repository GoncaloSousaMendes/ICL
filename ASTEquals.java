
public class ASTEquals implements ASTNode{

	ASTNode left, right;
	
	public IValue eval(Environ<IValue> env) throws UndeclaredIdentifierException, DuplicateIdentifierException 
	{ 
		if(((IntegerValue)left.eval(env)).getValue() == ((IntegerValue)right.eval(env)).getValue()){
			return new BooleanValue(true); 
		}
		return new BooleanValue(false); 
		
	}
	
	public Type typeCheck(Environ<Type> env) throws TypeErrorException{
		Type t1 = left.typeCheck(env);
		Type t2 = right.typeCheck(env);
		if ((t1==IntType.value && t2==IntType.value) || (t1==BoolType.value && t2==BoolType.value))
			return IntType.value;
		else
			throw new TypeErrorException(null);
	}
	
	 public ASTEquals(ASTNode l, ASTNode r)
     {
		left = l; right = r;
      }
	
	 @Override
 	public String toString(){
 		return left.toString() +" == "+ right.toString();
 	}

	public void compile(CodeBlock code, CompilerFrame env)throws UndeclaredIdentifierException, DuplicateIdentifierException  {
	//TODO: fazer compile
		//subtrair, se for zero, sair 1, cc 0
	}

}
