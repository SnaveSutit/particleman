package com.eliotlash.molang.variables;

import com.eliotlash.molang.ast.Assignable;
import com.eliotlash.molang.ast.Evaluator;
import com.eliotlash.molang.ast.Expr;
import com.eliotlash.molang.functions.Function;
import com.eliotlash.molang.functions.FunctionDefinition;
import com.eliotlash.molang.functions.classic.*;
import com.eliotlash.molang.functions.limit.Clamp;
import com.eliotlash.molang.functions.limit.Max;
import com.eliotlash.molang.functions.limit.Min;
import com.eliotlash.molang.functions.rounding.Ceil;
import com.eliotlash.molang.functions.rounding.Floor;
import com.eliotlash.molang.functions.rounding.Round;
import com.eliotlash.molang.functions.rounding.Trunc;
import com.eliotlash.molang.functions.utility.Lerp;
import com.eliotlash.molang.functions.utility.LerpRotate;
import com.eliotlash.molang.functions.utility.Random;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ExecutionContext {
    private Evaluator evaluator;
    public final Stack<Expr.Access> contextStack = new Stack<>();
    private Map<FunctionDefinition, Function> functionMap = new HashMap<>();
    private final Object2DoubleMap<RuntimeVariable> variableMap = new Object2DoubleOpenHashMap<>();
    private final Map<String, RuntimeVariable> variableCache = new HashMap<>();
    public final Object2DoubleMap<Assignable> assignableMap = new Object2DoubleOpenHashMap<>();
    public Object2DoubleMap<Assignable> functionScopedArguments = new Object2DoubleOpenHashMap<>();

    public ExecutionContext(Evaluator evaluator) {
        this.evaluator = evaluator;

        registerFunction("math", new Abs("abs"));
        registerFunction("math", new CosDegrees("cos"));
        registerFunction("math", new Cos("cosradians"));
        registerFunction("math", new SinDegrees("sin"));
        registerFunction("math", new Sin("sinradians"));
        registerFunction("math", new Exp("exp"));
        registerFunction("math", new Ln("ln"));
        registerFunction("math", new Mod("mod"));
        registerFunction("math", new Pow("pow"));
        registerFunction("math", new Sqrt("sqrt"));
        registerFunction("math", new Clamp("clamp"));
        registerFunction("math", new Max("max"));
        registerFunction("math", new Min("min"));
        registerFunction("math", new Ceil("ceil"));
        registerFunction("math", new Floor("floor"));
        registerFunction("math", new Round("round"));
        registerFunction("math", new Trunc("trunc"));
        registerFunction("math", new Lerp("lerp"));
        registerFunction("math", new LerpRotate("lerprotate"));
        registerFunction("math", new Random("random"));
    }

    public Evaluator getEvaluator() {
        return evaluator;
    }

    public Object2DoubleMap<RuntimeVariable> getVariableMap() {
        return variableMap;
    }

    public RuntimeVariable getCachedVariable(String var) {
        return variableCache.computeIfAbsent(var, name -> {
            String[] split = name.split("\\.", 2);

            VariableFlavor flavor = VariableFlavor.parse(split[0]);
            if (split.length == 2 && flavor != null) {
                return new RuntimeVariable(split[1], flavor);
            } else {
                return new RuntimeVariable(name, null);
            }
        });
    }

    public void setVariable(String var, double value) {
        RuntimeVariable cachedVariable = getCachedVariable(var);
        variableMap.put(cachedVariable, value);
    }

    public void registerFunction(String target, Function function) {
        functionMap.putIfAbsent(new FunctionDefinition(new Expr.Variable(target), function.getName()), function);
    }

    public void registerFunction(FunctionDefinition definition, Function function) {
        functionMap.putIfAbsent(definition, function);
    }

    public Function getFunction(FunctionDefinition definition) {
        return this.functionMap.get(definition);
    }
}
