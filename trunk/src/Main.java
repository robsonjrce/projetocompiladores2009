import java.io.*;
import Parser.*;
import syntaxtree.*;
import visitor.*;
import Translate.Translate;

public class Main { 
	public static void main(String args[])
	{
		try
		{	
			// Iniciamos um novo parser para a nossa gramatica
			FileInputStream fi = new FileInputStream(new File("src/Parser/GCD.mj"));
			System.setIn(fi);
			
			ParserMiniJava pmj = new ParserMiniJava(System.in); 
			
			Program p = pmj.Start();
			
			PrettyPrintVisitor ppv = new PrettyPrintVisitor();
			p.accept(ppv);
			System.out.println("Analise Terminada com Sucesso!");
			
			BuildSymbolTableVisitor stv = new BuildSymbolTableVisitor();
			p.accept(stv);
			p.accept(new TypeCheckVisitor(stv.getSymTab()));
			System.out.println("Checagem de Tipo Terminada com Sucesso!");
			
			Translate t = new Translate(p, new Mips.MipsFrame());
			p.accept(t);
			System.out.println("Traducao Terminada com Sucesso!");
			t.printResults();
		}
		catch (ParseException e) 
		{      
			System.out.println(e.toString());
		}
		catch (java.io.FileNotFoundException e)  
		{  
			System.out.println(e.toString());  
		}  
	}
}


