package org.jf.util.jcommander;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;
import com.beust.jcommander.Parameterized;
import com.beust.jcommander.Parameters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class ExtendedCommands {

    @Nonnull
    private static ExtendedParameters getExtendedParameters(Object command) {
        ExtendedParameters anno = command.getClass().getAnnotation(ExtendedParameters.class);
        if (anno == null) {
            throw new IllegalStateException("All extended commands should have an ExtendedParameters annotation: " +
                    command.getClass().getCanonicalName());
        }
        return anno;
    }

    @Nonnull
    public static String commandName(JCommander jc) {
        return getExtendedParameters(jc.getObjects().get(0)).commandName();
    }

    @Nonnull
    public static String commandName(Object command) {
        return getExtendedParameters(command).commandName();
    }

    @Nonnull
    public static String[] commandAliases(JCommander jc) {
        return commandAliases(jc.getObjects().get(0));
    }

    @Nonnull
    public static String[] commandAliases(Object command) {
        return getExtendedParameters(command).commandAliases();
    }

    public static boolean includeParametersInUsage(JCommander jc) {
        return includeParametersInUsage(jc.getObjects().get(0));
    }

    public static boolean includeParametersInUsage(Object command) {
        return getExtendedParameters(command).includeParametersInUsage();
    }

    @Nonnull
    public static String postfixDescription(JCommander jc) {
        return postfixDescription(jc.getObjects().get(0));
    }

    @Nonnull
    public static String postfixDescription(Object command) {
        return getExtendedParameters(command).postfixDescription();
    }

    public static void addExtendedCommand(JCommander jc, Command command) {
        jc.addCommand(commandName(command), command, commandAliases(command));
        command.setupCommand(command.getJCommander());
    }

    @Nonnull
    public static String[] parameterArgumentNames(ParameterDescription parameterDescription) {
        if (parameterDescription == null) {
            return new String[0];
        }
        
        Parameterized parameterized = parameterDescription.getParameterized();
        Class<?> cls = parameterDescription.getObject().getClass();
        Field field = null;
        
        while (cls != Object.class) {
            try {
                field = cls.getDeclaredField(parameterized.getName());
                break;
            } catch (NoSuchFieldException ex) {
                cls = cls.getSuperclass();
            }
        }

        if (field == null) {
            return new String[0];
        }
        
        ExtendedParameter extendedParameter = field.getAnnotation(ExtendedParameter.class);
        if (extendedParameter != null) {
            return extendedParameter.argumentNames();
        }

        return new String[0];
    }

    @Nullable
    public static JCommander getSubcommand(JCommander jc, String commandName) {
        if (jc.getCommands().containsKey(commandName)) {
            return jc.getCommands().get(commandName);
        } else {
            for (JCommander command : jc.getCommands().values()) {
                for (String alias : commandAliases(command)) {
                    if (commandName.equals(alias)) {
                        return command;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public static String getCommandDescription(@Nonnull JCommander jc) {
        if (jc.getObjects().size() > 0) {
            Object commandObj = jc.getObjects().get(0);
            Parameters params = commandObj.getClass().getAnnotation(Parameters.class);
            if (params != null) {
                return params.commandDescription();
            }
        }
        return null;
    }
    
    @Nullable
    public static ParameterDescription getMainParameter(@Nonnull JCommander jc) {
        for (ParameterDescription param : jc.getParameters()) {
            if (param.getParameter().names().length == 0) {
                return param;
            }
        }
        return null;
    }
}