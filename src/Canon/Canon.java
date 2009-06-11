package Canon;

import Temp.Label;
import Temp.Temp;

import Tree.*;
import IR_visitor.*;


@SuppressWarnings("unused")
public final class Canon {

   private static class MoveCall extends Stm {
      final TEMP dst;
      final CALL src;
      MoveCall (TEMP d, CALL s) {dst=d; src=s;}
      public ExpList kids() {return src.kids();}
      public Stm build (ExpList kids) {
         return new MOVE (dst, src.build(kids));
      }
	public String accept(StringVisitor v) {return null;}
	public void accept(IntVisitor v, int d) {}
	public void accept(TempVisitor v) {}
   }   
  
   private static class ExpCall extends Stm {
      final CALL call;
      ExpCall (CALL c) {call=c;}
      public ExpList kids() {return call.kids();}
      public Stm build(ExpList kids) {
         return new Tree.EXP1 (call.build(kids));
      }
	public String accept(StringVisitor v) {return null;}
	public void accept(IntVisitor v, int d) {}
	public void accept(TempVisitor v) {}
   }   


   private static final class StmExpList {
      final Stm stm;
      final ExpList exps;
      StmExpList (Stm s, ExpList e) {stm=s; exps=e;}
   }

   static boolean isNop (Stm a) {
      return a instanceof EXP1 && ((EXP1)a).exp instanceof CONST;
   }

   private static Stm seq (final Stm a, final Stm b) {
      if (isNop(a)) return b;
      else if (isNop(b)) return a;
      else return new SEQ(a,b);
   }

   private static Stm seq (final Stm a, final Stm b, final Stm c) {
      return (seq (a, seq (b, c)));
   }

   private static boolean commute (Stm a, Exp b) {
      return isNop(a) || b instanceof NAME || b instanceof CONST;
   }

   static Stm do_stm (SEQ s) { 
      return seq (do_stm(s.left), do_stm(s.right));
   }

   private static Stm do_stm (MOVE s) { 
      assert (s!=null);
      if (s.dst instanceof TEMP && s.src instanceof CALL) 
	 return reorder_stm (new MoveCall((TEMP)s.dst, (CALL)s.src));
      else if (s.dst instanceof ESEQ)
	 return do_stm (new SEQ(((ESEQ)s.dst).stm, new MOVE(((ESEQ)s.dst).exp, s.src)));
      else return reorder_stm (s);
   }

   static Stm do_stm (EXP1 s) {
      assert (s!=null);
      if (s.exp instanceof CALL)
	 return reorder_stm (new ExpCall((Tree.CALL)s.exp));
      else return reorder_stm(s);
   }

   static private Stm do_stm (final Stm s) {
      if (s instanceof SEQ) return do_stm ((SEQ)s);
      else if (s instanceof MOVE) return do_stm ((MOVE)s);
      // else if (s instanceof EXP) throw new RuntimeException ("EXP changed to EXP1");
      else if (s instanceof EXP1) return do_stm ((EXP1)s);
      else return reorder_stm(s);
   }

   private static Stm reorder_stm (final Stm s) {
      assert (s!=null);
      final StmExpList x = reorder (s.kids());
      assert (x!=null);
      return seq (x.stm, s.build (x.exps));
   }

   private static ESEQ do_exp (final ESEQ e) {
      final Stm stms = do_stm (e.stm);
      final ESEQ b = do_exp (e.exp);
      return new ESEQ (seq(stms,b.stm), b.exp);
   }

   private static ESEQ do_exp (Exp e) {
      if (e instanceof Tree.ESEQ) return do_exp((Tree.ESEQ)e);
      else return reorder_exp(e);
   }
         
   private static ESEQ reorder_exp (final Exp e) {
      final StmExpList x = reorder(e.kids());
      return new ESEQ (x.stm, e.build(x.exps));
   }

   private static final StmExpList nopNull = new StmExpList(new EXP1(new Tree.CONST(0)),null);

   static StmExpList reorder (ExpList exps) {
      if (exps==null) {
	 return nopNull;
      } else {
	 final Exp a = exps.head;
	 if (a instanceof Tree.CALL) {
	    final Temp t = new Temp();
	    final Exp e = new Tree.ESEQ(new Tree.MOVE(new Tree.TEMP(t), a),
				       new Tree.TEMP(t));
	    return reorder(new ExpList(e, exps.tail));
	 } else {
	    Tree.ESEQ aa = do_exp(a);
	    final StmExpList bb = reorder(exps.tail);
	    if (commute(bb.stm, aa.exp))
	       return new StmExpList(seq(aa.stm,bb.stm), 
				     new ExpList(aa.exp,bb.exps));
	    else {
	       final Temp t = new Temp();
	       return new StmExpList(
				     seq(aa.stm, new Tree.MOVE(new TEMP(t),aa.exp), bb.stm),
				     new ExpList(new TEMP(t), bb.exps));
	    }
	 }
      }
   }
        
   private static StmList linear (SEQ s, StmList l) {
      return linear (s.left,linear(s.right,l));
   }

   private static StmList linear (final Stm s, StmList l) {
      if (s instanceof SEQ) return linear((SEQ)s, l);
      else return new StmList(s,l);
   }

   public static StmList linearize (final Stm s) {
      assert (s!=null);
      return linear (do_stm(s), null);
   }
}
