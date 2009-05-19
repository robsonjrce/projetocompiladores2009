package Canon;

public class StmListList {
  public Tree.StmList head;
  public StmListList tail;
  public StmListList(Tree.StmList h, StmListList t) {head=h; tail=t;}
  public int length () {
      if (tail==null) return 1; else return 1 + tail.length();
   }

   public String toString () { return toString(0); }
   public String toString (int i) {
      if (tail==null) {
         return String.format ("--Block %d--%n%s", i+1, head);
      } else {
         return String.format ("--Block %d--%n%s%n", i+1, head, tail);
      }
   }

}

