package org.jf.util.jcommander;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;
import com.beust.jcommander.Parameters;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.jf.util.ConsoleUtil;
import org.jf.util.WrappedIndentingWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

public class HelpFormatter {
    private int width = 80;

    @Nonnull
    public HelpFormatter width(int width) {
        this.width = width;
        return this;
    }

    @Nonnull
    private static ExtendedParameters getExtendedParameters(JCommander jc) {
        ExtendedParameters anno = jc.getObjects().get(0).getClass().getAnnotation(ExtendedParameters.class);
        if (anno == null) {
            throw new IllegalStateException("All commands should have an ExtendedParameters annotation");
        }
        return anno;
    }

    @Nonnull
    private static List<String> getCommandAliases(JCommander jc) {
        return Lists.newArrayList(getExtendedParameters(jc).commandAliases());
    }

    private static boolean includeParametersInUsage(@Nonnull JCommander jc) {
        return getExtendedParameters(jc).includeParametersInUsage();
    }

    @Nonnull
    private static String getPostfixDescription(@Nonnull JCommander jc) {
        return getExtendedParameters(jc).postfixDescription();
    }

    private int getParameterArity(ParameterDescription param) {
        if (param == null) return 0;
        if (param.getParameter().arity() > 0) {
            return param.getParameter().arity();
        }
        Class<?> type = param.getParameterized().getType();
        if (type == boolean.class || type == Boolean.class) {
            return 0;
        }
        return 1;
    }

    private List<ParameterDescription> getSortedParameters(JCommander jc) {
        List<ParameterDescription> parameters = Lists.newArrayList(jc.getParameters());
        parameters.removeIf(param -> param.getParameter().names().length == 0);
        
        parameters.sort((o1, o2) -> {
            String s1 = o1.getParameter().names()[0].replaceAll("^-*", "");
            String s2 = o2.getParameter().names()[0].replaceAll("^-*", "");
            return s1.compareTo(s2);
        });
        
        return parameters;
    }

    @Nonnull
    public String format(@Nonnull JCommander... jc) {
        return format(Arrays.asList(jc));
    }

    @Nonnull
    public String format(@Nonnull List<JCommander> commandHierarchy) {
        try {
            StringWriter stringWriter = new StringWriter();
            WrappedIndentingWriter writer = new WrappedIndentingWriter(stringWriter, width - 5, width);

            JCommander leafJc = Iterables.getLast(commandHierarchy);

            writer.write("usage:");
            writer.indent(2);

            for (JCommander jc : commandHierarchy) {
                writer.write(" ");
                writer.write(ExtendedCommands.commandName(jc));
            }

            if (includeParametersInUsage(leafJc)) {
                for (ParameterDescription param : leafJc.getParameters()) {
                    if (!param.getParameter().hidden() && param.getParameter().names().length > 0) {
                        writer.write(" [");
                        writer.write(param.getParameter().names()[0]);
                        writer.write("]");
                    }
                }
            } else {
                if (!leafJc.getParameters().isEmpty()) {
                    writer.write(" [<options>]");
                }
            }

            if (!leafJc.getCommands().isEmpty()) {
                writer.write(" [<command [<args>]]");
            }

            ParameterDescription mainParam = ExtendedCommands.getMainParameter(leafJc);
            if (mainParam != null) {
                String[] argumentNames = ExtendedCommands.parameterArgumentNames(mainParam);
                if (argumentNames.length == 0) {
                    writer.write(" <args>");
                } else {
                    String argumentName = argumentNames[0];
                    boolean writeAngleBrackets = !argumentName.startsWith("<") && !argumentName.startsWith("[");
                    writer.write(" ");
                    if (writeAngleBrackets) {
                        writer.write("<");
                    }
                    writer.write(argumentNames[0]);
                    if (writeAngleBrackets) {
                        writer.write(">");
                    }
                }
            }

            writer.deindent(2);

            String commandDescription = ExtendedCommands.getCommandDescription(leafJc);
            if (commandDescription != null) {
                writer.write("\n");
                writer.write(commandDescription);
            }

            if (!leafJc.getParameters().isEmpty() || mainParam != null) {
                writer.write("\n\nOptions:");
                writer.indent(2);
                
                for (ParameterDescription param : getSortedParameters(leafJc)) {
                    if (!param.getParameter().hidden()) {
                        writer.write("\n");
                        writer.indent(4);
                        
                        if (param.getParameter().names().length > 0) {
                            writer.write(Joiner.on(',').join(param.getParameter().names()));
                        }
                        
                        if (getParameterArity(param) > 0) {
                            String[] argumentNames = ExtendedCommands.parameterArgumentNames(param);
                            for (int i = 0; i < getParameterArity(param); i++) {
                                writer.write(" ");
                                if (i < argumentNames.length && !argumentNames[i].isEmpty()) {
                                    writer.write("<");
                                    writer.write(argumentNames[i]);
                                    writer.write(">");
                                } else {
                                    writer.write("<arg>");
                                }
                            }
                        }
                        
                        if (param.getDescription() != null && !param.getDescription().isEmpty()) {
                            writer.write(" - ");
                            writer.write(param.getDescription());
                        }
                        
                        if (param.getDefault() != null) {
                            String defaultValue = null;
                            if (param.getParameterized().getType() == Boolean.class ||
                                param.getParameterized().getType() == Boolean.TYPE) {
                                if ((Boolean) param.getDefault()) {
                                    defaultValue = "True";
                                }
                            } else if (List.class.isAssignableFrom(param.getParameterized().getType())) {
                                if (!((List<?>) param.getDefault()).isEmpty()) {
                                    defaultValue = param.getDefault().toString();
                                }
                            } else {
                                defaultValue = param.getDefault().toString();
                            }
                            if (defaultValue != null) {
                                writer.write(" (default: ");
                                writer.write(defaultValue);
                                writer.write(")");
                            }
                        }
                        writer.deindent(4);
                    }
                }

                if (mainParam != null) {
                    String[] argumentNames = ExtendedCommands.parameterArgumentNames(mainParam);
                    writer.write("\n");
                    writer.indent(4);
                    if (argumentNames.length > 0 && !argumentNames[0].isEmpty()) {
                        writer.write("<");
                        writer.write(argumentNames[0]);
                        writer.write(">");
                    } else {
                        writer.write("<args>");
                    }

                    if (mainParam.getDescription() != null) {
                        writer.write(" - ");
                        writer.write(mainParam.getDescription());
                    }
                    writer.deindent(4);
                }
                writer.deindent(2);
            }

            if (!leafJc.getCommands().isEmpty()) {
                writer.write("\n\nCommands:");
                writer.indent(2);

                List<Map.Entry<String, JCommander>> entryList = Lists.newArrayList(leafJc.getCommands().entrySet());
                entryList.sort(Comparator.comparing(Map.Entry::getKey));

                for (Map.Entry<String, JCommander> entry : entryList) {
                    String commandName = entry.getKey();
                    JCommander command = entry.getValue();

                    Object arg = command.getObjects().get(0);
                    Parameters parametersAnno = arg.getClass().getAnnotation(Parameters.class);
                    if (parametersAnno == null || !parametersAnno.hidden()) {
                        writer.write("\n");
                        writer.indent(4);
                        writer.write(commandName);
                        
                        List<String> aliases = getCommandAliases(command);
                        if (!aliases.isEmpty()) {
                            writer.write("(");
                            writer.write(Joiner.on(',').join(aliases));
                            writer.write(")");
                        }

                        String commandDesc = ExtendedCommands.getCommandDescription(command);
                        if (commandDesc != null) {
                            writer.write(" - ");
                            writer.write(commandDesc);
                        }
                        writer.deindent(4);
                    }
                }
                writer.deindent(2);
            }

            String postfixDescription = getPostfixDescription(leafJc);
            if (!postfixDescription.isEmpty()) {
                writer.write("\n\n");
                writer.write(postfixDescription);
            }

            writer.flush();
            return stringWriter.getBuffer().toString();
            
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}