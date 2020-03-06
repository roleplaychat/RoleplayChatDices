package ru.xunto.roleplaychat.dices.forge_1_7_10.parser.result;

import ru.xunto.roleplaychat.dices.forge_1_7_10.parser.IResult;
import ru.xunto.roleplaychat.dices.forge_1_7_10.parser.colored.ColoredBuilder;

public class ResultSum implements IResult {
    private final IResult result1;
    private final IResult result2;

    public ResultSum(IResult result1, IResult result2) {
        this.result1 = result1;
        this.result2 = result2;
    }

    @Override
    public int getFinalResult() {
        return this.result1.getFinalResult() + this.result2.getFinalResult();
    }

    @Override
    public ColoredBuilder getColoredResult() {
        ColoredBuilder coloredBuilder = new ColoredBuilder();
        return this.getColoredResult(coloredBuilder);
    }

    @Override
    public ColoredBuilder getColoredResult(ColoredBuilder builder) {
        result1.getColoredResult(builder);
        builder.add("+");
        result2.getColoredResult(builder);

        return builder;
    }
}
