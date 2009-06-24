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
		  // testando entrada para arquivo
		  if (args.length > 0)
		  {
		    File input = new File(args[0]);
  			FileInputStream fi = new FileInputStream(input);
  			System.setIn(fi);
		  }

      // Iniciamos um novo parser para a nossa gramatica
			ParserMiniJava pmj = new ParserMiniJava(System.in); 
			Program p = pmj.Start();

			// Imprimindo o resultado do parser
			PrettyPrintVisitor ppv = new PrettyPrintVisitor();
			p.accept(ppv);
			System.out.println("Analise Terminada com Sucesso!");
			
			// Checagem de tipo
			BuildSymbolTableVisitor stv = new BuildSymbolTableVisitor();
			p.accept(stv);
			p.accept(new TypeCheckVisitor(stv.getSymTab()));
			System.out.println("Checagem de Tipo Terminada com Sucesso!");
			
			// Traducao do codigo
			Translate t = new Translate(p, new Mips.MipsFrame());
			p.accept(t);
			System.out.println("Traducao Terminada com Sucesso!");
			
			// Imprimindo traducao de codigo
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


