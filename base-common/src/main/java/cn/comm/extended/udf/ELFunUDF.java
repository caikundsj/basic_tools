package cn.comm.extended.udf;


import com.googlecode.aviator.AviatorEvaluator;
import org.apache.flink.table.functions.ScalarFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * EL表达式解析判断
 */
public final class ELFunUDF extends ScalarFunction {

    public Boolean eval(String elStr, Map<String, Double> point) {
        Boolean re = Boolean.FALSE;
        try {
            re = (Boolean) AviatorEvaluator.compile(elStr).execute(new HashMap<>(point));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

}
