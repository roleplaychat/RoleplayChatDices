package ru.xunto.roleplaychat.dices;

import org.jparsec.error.ParserException;
import ru.xunto.roleplaychat.RoleplayChatCore;
import ru.xunto.roleplaychat.api.ICommand;
import ru.xunto.roleplaychat.api.ISpeaker;
import ru.xunto.roleplaychat.dices.parser.DiceParser;
import ru.xunto.roleplaychat.dices.parser.IResult;
import ru.xunto.roleplaychat.dices.parser.IRoll;
import ru.xunto.roleplaychat.features.middleware.distance.Distance;
import ru.xunto.roleplaychat.features.middleware.distance.DistanceMiddleware;
import ru.xunto.roleplaychat.framework.api.Environment;
import ru.xunto.roleplaychat.framework.api.Request;
import ru.xunto.roleplaychat.framework.commands.CommandException;
import ru.xunto.roleplaychat.framework.pebble.PebbleChatTemplate;
import ru.xunto.roleplaychat.framework.renderer.ITemplate;
import ru.xunto.roleplaychat.framework.renderer.text.TextColor;
import ru.xunto.roleplaychat.framework.state.IProperty;
import ru.xunto.roleplaychat.framework.state.MessageState;
import ru.xunto.roleplaychat.framework.state.Property;
import ru.xunto.roleplaychat.framework.state.values.colored_array.ColoredText;

import java.util.List;

public class RollCommand implements ICommand {
    public static IProperty<IRoll> roll = new Property<>("roll");
    public static IProperty<List<ColoredText>> result = new Property<>("result");
    public static IProperty<Integer> finalResult = new Property<>("final_result");

    private static ITemplate template = new PebbleChatTemplate("assets/roleplaychatdices/templates/dices.twig");

    public void sendDiceResult(ISpeaker speaker, IRoll roll, IResult result) {
        Request request = new Request("", speaker);
        Environment environment = new Environment(speaker.getName(), "");
        MessageState state = environment.getState();
        environment.setProcessed(true);
        environment.setTemplate(template);
        environment.getColors().put("default", TextColor.GRAY);
        environment.getColors().put("critical_success", TextColor.GREEN);
        environment.getColors().put("critical_failure", TextColor.RED);

        state.setValue(RollCommand.roll, roll);
        state.setValue(RollCommand.result, result.getColoredResult().build());
        state.setValue(RollCommand.finalResult, result.getFinalResult());
        state.setValue(DistanceMiddleware.FORCE_ENVIRONMENT, true);
        state.setValue(DistanceMiddleware.DISTANCE, Distance.NORMAL);

        RoleplayChatCore.instance.process(request, environment);
    }

    @Override
    public String getCommandName() {
        return "roll";
    }

    @Override
    public String[] getTabCompletion(ISpeaker iSpeaker, String[] strings) {
        return new String[0];
    }

    @Override
    public boolean canExecute(ISpeaker iSpeaker) {
        return true;
    }

    @Override
    public void execute(ISpeaker speaker, String[] args) throws CommandException {
        String arg = String.join("", args);

        IRoll roll;
        try {
            roll = DiceParser.parser().parse(arg);
            IResult result = roll.roll();
            this.sendDiceResult(speaker, roll, result);
        } catch (ParserException e) {
            throw new CommandException("Невозможно бросить такой дайс.");
        }
    }
}
