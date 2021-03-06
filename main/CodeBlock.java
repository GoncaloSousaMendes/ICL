package main;


import java.io.PrintWriter;
import java.util.ArrayList;

public class CodeBlock {
	
	protected ArrayList<CompilerFrame> frames;
	//ArrayList<CompilerFrame> frames;
	protected ArrayList<String> code;
	protected static int currentLabel;
	protected static int currentClosure;
	
	CodeBlock(){
		code = new ArrayList<String>();
		frames = new ArrayList<CompilerFrame>();
		currentLabel = 0;
		currentClosure = 0;
	}
	
	public int labelGenarator(){
		return currentLabel++;
	}

	public void emit_label (int label){
		code.add("L" + label + ":");
	}
	
	public void emit_ifeq(int label){
		code.add("ifeq L" + label);
	}
	
	public void emit_goto(int label1){
		code.add("goto L" + label1);
	}
	
	public void emit_equals(){
		code.add("isub");
		int label = this.labelGenarator();
		code.add("ifeq L_" + label);
		emit_push(0);
		int label2 = this.labelGenarator();
		code.add("goto L_" + label2);
		code.add("L_" + label + ":");
		emit_push(1);
		code.add("L_" + label2+":");
	}
	
	public void emit_dif(){
		code.add("isub");
		int label = this.labelGenarator();
		code.add("ifeq L_" + label);
		emit_push(1);
		int label2 = this.labelGenarator();
		code.add("goto L_" + label2);
		code.add("L_" + label + ":");
		emit_push(0);
		code.add("L_" + label2+":");
	}
	
	public void emit_compare(){
		code.add("isub");
		int label = this.labelGenarator();
		code.add("ifgt L_" + label);
		emit_push(0);
		int label2 = this.labelGenarator();
		code.add("goto L_" + label2);
		code.add("L_" + label + ":");
		emit_push(1);
		code.add("L_" + label2+":");
	}
	
	public void emit_pop(){
		code.add("pop");
	}
	
	public void emit_and(){
		code.add("iand");
	}
	
	public void emit_or(){
		code.add("ior");
	}
	
	public void emit_push(int n){
		code.add("sipush "+n);
	}
	
	public void emit_xor(){
		code.add("ixor");
	}
	
	public void emit_add(){
		code.add("iadd");
	}
	
	public void emit_mul(){
		code.add("imul");
	}
	
	public void emit_sub(){
		code.add("isub");
	}
	
	public void emit_div(){
		code.add("idiv");
	}
	
	public void comment (String comment){
		code.add(";" + comment);
	}
	
	public void loadFrame(CompilerFrame env){
		emit_aload();
		code.add("checkcast frame_" + env.getType());
	}
	
	public void getFrame(int frame, int father) {
		code.add("getfield frame_" + frame + "/SL Lframe_" + father + ";");
	}
	
	public void emit_getfield(int frame, String id, String type){
		code.add("getfield frame_" + frame + "/loc_" + id + " " + type);
	}
	
	public void newFrame(int type, CompilerFrame env){
		comment("create a new frame");
		code.add("new frame_" + type);
		code.add("dup");
		code.add("invokespecial frame_" + type + "/<init>()V");
		code.add("dup");	
		//if(type != 1){
		if(type > 1 && env.getAncestor().getType() != 0){
			emit_aload();
			code.add("putfield frame_" + type + "/SL Lframe_" + env.getAncestor().getType() + ";");
			code.add("dup");
		}
		frames.add(env);
	}
	
	public void endFrame(int frame, int father){

		if(frame > 1 && father != 0){
			emit_aload();
			code.add("checkcast frame_" + frame);
			code.add("getfield frame_" + frame + "/SL Lframe_"+father+";");
			emit_astore();
		}

		if(frame == 1){
			code.add("aconst_null");
			emit_astore();
		}
	}
	
	public void emit_putField(int frame, String id, String type){
		code.add("putfield frame_" + frame + "/loc_" + id + " " + type);
	}
	
	public void emit_checkTypeCheck(String type){
		code.add("checkcast "+type);
	}

	public void emit_refInt(){
		code.add("new ref_int");
		code.add("dup");
		code.add("invokespecial ref_int/<init>()V");
		code.add("dup");	
	}
	
	public void emit_putFieldRefInt(){
		code.add("putfield ref_int/v I");
	}
	
	public void emit_CheckCastRefInt(){
		code.add("checkcast ref_int");
	}
	
	public void emit_getFieldForRefInt(){
		code.add("getfield ref_int/v I");
	}
	
	public void emit_refClass(){
		code.add("new ref_class");
		code.add("dup");
		code.add("invokespecial ref_class/<init>()V");
		code.add("dup");	
	}

	public void emit_putFieldRefClass(){
		code.add("putfield ref_class/v Ljava/lang/Object;");
	}

	public void emit_CheckCastRefClass(){
		code.add("checkcast ref_class");
	}

	public void emit_getFieldForRefClass(){
		code.add("getfield ref_class/v Ljava/lang/Object;");
	}
	
	public void emit_aload(){
		code.add("aload 1");
	}
	
	public void emit_astore(){
		code.add("astore 1");
	}
	
	public void emit_dup(){
		code.add("dup");
	}
	
	public void emit_Fun(){
		code.add("new closure_f_"+currentClosure);
		this.emit_dup();
		//TODO: mudar o SL para o local onde guarda 
		//o SL corrente
		code.add("aload SL");
		code.add("putfield closure_f_" + currentClosure +"SL type;");
		
	}
	
	
	public void dump(){
		createDemo();
		createFrames();
		createInterface();
		creatRef();
	}

	//Criar ficheiros
	public void createDemo(){
		try{
			PrintWriter out = new PrintWriter("Demo.j");
			createHeader(out);
			createCode(out);
			createFooter(out);
			out.close();
		} catch (Exception e){
			//e.printStackTrace();
		}
	}
	
	void createHeader(PrintWriter out){
		out.println(".class public Demo");
		out.println(".super java/lang/Object");
		out.println("; standard initializer");
		out.println(".method public <init>()V");
		out.println("   aload_0");
		out.println("   invokenonvirtual java/lang/Object/<init>()V");
		out.println("   return");
		out.println(".end method");
		out.println("");
		out.println(".method public static main([Ljava/lang/String;)V");
		out.println("       ; set limits used by this method");
		out.println("       .limit locals 10	");
		out.println("       .limit stack 256");
		out.println("");
		out.println("       ; setup local variables:");
		out.println("");
		out.println("       ;    1 - the PrintStream object held in java.lang.System.out");
		out.println("       getstatic java/lang/System/out Ljava/io/PrintStream;");
		out.println("");
		out.println("       ; place your bytecodes here");
		out.println("       ; START");
	}

	void createFooter(PrintWriter out){
		out.println("	       ; END");
		out.println("");
		out.println("");
		out.println("	       ; convert to String;");
		out.println("	       invokestatic java/lang/String/valueOf(I)Ljava/lang/String;");
		out.println("	       ; call println ");
		out.println("	       invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V");
		out.println("");
		out.println("	       return");
		out.println("");
		out.println("	.end method");
	}
	
	void createCode (PrintWriter out){
		for (String s : code)
			out.println(s);
	}

	void createFrames(){
		
		for(CompilerFrame frame: frames){
			try{
				PrintWriter out = new PrintWriter("frame_" + frame.getType()+".j");
				 
				out.println(".source frame_" + frame.getType() + ".j");
				out.println(".class frame_" + frame.getType());
				out.println(".super java/lang/Object");
				out.println(".implements frame");
				out.println();
				
				if(frame.getType() != 1 && frame.getAncestor().getType() != 0){
					out.println(".field public SL Lframe_" + frame.getAncestor().getType() + ";");
					out.println();
				}
				
				ArrayList <String> t = frame.getTypes();
				int i = 0;
				for(String s: frame.getAssoc()){
					out.println(".field public loc_" + s + " "+ t.get(i));
					i++;
				}
				out.println();
				out.println(".method public <init>()V");
				out.println("aload_0");
				out.println("invokespecial java/lang/Object/<init>()V");
				out.println("return");
				out.println(".end method");
			out.close();
			}catch(Exception e){
				//e.printStackTrace();
			}
		}
	}
	
	
	void creatRef(){
		creatRefInt();
		creatRefClass();
	}
	
	void creatRefInt(){
		try{
			PrintWriter out = new PrintWriter("ref_int.j");
			out.println(".class ref_int");
			out.println(".super java/lang/Object");
			out.println(".field public v I");
			out.println();
			out.println(".method public <init>()V");
			out.println("aload_0");
			out.println("invokespecial java/lang/Object/<init>()V");
			out.println("return");
			out.println(".end method");
			out.close();
		} catch (Exception e){
			//e.printStackTrace();
		}
	}
	
	void creatRefClass(){
		try{
			PrintWriter out = new PrintWriter("ref_class.j");
			out.println(".class ref_class");
			out.println(".super java/lang/Object");
			out.println(".field public v Ljava/lang/Object;");
			out.println();
			out.println(".method public <init>()V");
			out.println("aload_0");
			out.println("invokespecial java/lang/Object/<init>()V");
			out.println("return");
			out.println(".end method");
			out.close();
		} catch (Exception e){
			//e.printStackTrace();
		}
	}
	
	void createInterface(){
		try{
			PrintWriter out = new PrintWriter("frame.j");
			out.println(".source frame.j");
			out.println(".interface public frame");
			out.println(".super java/lang/Object");
			out.close();
		}catch(Exception e){
			//e.printStackTrace();
		}
		
		
		/*
		 * closure_interface_type_t002
		 */
		try{
			PrintWriter out = new PrintWriter("closure_interfaceClosure.j");
			out.println(".source frame.j");
			out.println(".interface public frame");
			out.println(".super java/lang/Object");
			out.close();
		}catch(Exception e){
			//e.printStackTrace();
		}
		
		
	}
}
