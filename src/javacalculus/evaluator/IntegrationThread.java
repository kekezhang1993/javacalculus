/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javacalculus.evaluator;

import java.util.concurrent.Callable;
import javacalculus.core.CALC;
import javacalculus.struct.CalcObject;
import javacalculus.struct.CalcSymbol;

/**
 *
 * This class is used to process one of the many possibilities generated by the INTBYPARTS class
 * 
 * @author Seva
 */
public final class IntegrationThread implements Callable {

    private CalcObject[] udvPair;
    private CalcSymbol var;
    private CalcINTBYPARTS father;
    private int depth;

    public IntegrationThread(CalcObject[] pair, CalcSymbol var, CalcINTBYPARTS father, int depth) {
        udvPair = pair;
        this.var = var;
        this.father = father;
        this.depth = depth;
    }

    @Override
    public String toString() {
        return "I am " + Thread.currentThread().getName();
    }

    @Override
    public Object call() throws Exception {
        if (depth < CALC.max_recursion_depth) {
            if (father.keepGoing()) {
                try {
                    CalcObject u = udvPair[0];
                    CalcObject dv = udvPair[1];
                    CalcINT vIntegrator = new CalcINT(depth);
                    CalcObject v = CALC.SYM_EVAL(vIntegrator.integrate(dv, var));
                    u = CALC.SYM_EVAL(u);
                    v = CALC.SYM_EVAL(v);
                    //System.out.println("This is our u: " + u);
                    //System.out.println("This is our v: " + v);
                    //we should have a non null u and dv here
                    CalcObject du = CALC.SYM_EVAL(CALC.DIFF.createFunction(u, var));
                    //System.out.println("This is our du: " + du);
                    CalcObject uTimesV = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(u, v));
                    CalcObject vTimesDu = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(v, du));
                    CalcINT vduIntegrator = new CalcINT(depth);
                    //System.out.println(toString());
                    CalcObject answer = CALC.SYM_EVAL(CALC.ADD.createFunction(uTimesV, CALC.MULTIPLY.createFunction(vduIntegrator.integrate(vTimesDu, var), CALC.NEG_ONE)));
                    //System.out.println(answer);
                    if (!containsError(answer)) {
                        return answer;
                    }
                } catch (Exception e) {
                }
            }
            throw new Exception();
        } else {
            throw new Exception();
        }
    }

    public boolean containsError(CalcObject test) {
        return test == null || test.toString().contains(CALC.ERROR.toString());
    }
}
