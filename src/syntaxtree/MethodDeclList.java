package syntaxtree;
import java.util.Vector;

public class MethodDeclList 
{
   private Vector list;
   public MethodDeclList() {
      list = new Vector();
   }
   public void addElement(Exp n) {
      list.addElement(n);
   }
   public Exp elementAt(int i)  { 
      return (Exp)list.elementAt(i); 
   }
   public int size() { 
      return list.size(); 
   }
}
