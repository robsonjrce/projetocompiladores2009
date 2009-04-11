package syntaxtree;
import java.util.Vector;

public class StatementList 
{
   private Vector list;
   public StatementList() {
      list = new Vector();
   }
   public void addElement(Statement n) {
      list.addElement(n);
   }
   public Exp elementAt(int i)  { 
      return (Exp)list.elementAt(i); 
   }
   public int size() { 
      return list.size(); 
   }
}