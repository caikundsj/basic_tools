package cn.comm.extended.udf;


import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Options;
import org.apache.flink.table.functions.FunctionContext;
import org.apache.flink.table.functions.ScalarFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * EL表达式解析判断
 * return: String
 */
public class ELFunUDF extends ScalarFunction {

    private static final long serialVersionUID = 3054479359028126443L;

    public static String eval(String elStr, Map<String, Double> point, String var) {
        try {
            var = (String) AviatorEvaluator.compile(elStr, true).execute(new HashMap<>(point));
        } catch (Exception e) {
            return var;
        }
        return var;
    }

    public static Boolean eval(String elStr, Map<String, Double> point, Boolean b) {
        try {
            b = (Boolean) AviatorEvaluator.compile(elStr, true).execute(new HashMap<>(point));
        } catch (Exception e) {
            return b;
        }
        return b;
    }

    public static Integer eval(String elStr, Map<String, Double> point, Integer i) {
        try {
            i = (Integer) AviatorEvaluator.compile(elStr, true).execute(new HashMap<>(point));
        } catch (Exception e) {
            return i;
        }
        return i;
    }

    public static Double eval(String elStr, Map<String, Double> point, Double d) {
        try {
            d = (Double) AviatorEvaluator.compile(elStr, true).execute(new HashMap<>(point));
        } catch (Exception e) {
            return d;
        }
        return d;
    }

    @Override
    public void open(FunctionContext context) throws Exception {
        AviatorEvaluator.setOption(Options.OPTIMIZE_LEVEL, AviatorEvaluator.COMPILE);
        AviatorEvaluator.setOption(Options.MAX_LOOP_COUNT, 2000);
        super.open(context);
    }


}