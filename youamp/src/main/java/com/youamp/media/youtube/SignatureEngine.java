package com.youamp.media.youtube;

import javax.script.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignatureEngine {

    private String mDecipherJsFileName;
    private String mDecipherFunctionName;
    private String mDecipherFunctions;

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/97.0.4692.98 Safari/537.36";

    private static final Pattern patDecryptionJsFile = Pattern.compile("\\\\/s\\\\/player\\\\/([^\"]+?)\\.js");
    private static final Pattern patDecryptionJsFileWithoutSlash = Pattern.compile("/s/player/([^\"]+?).js");
    private static final Pattern patSignatureDecFunction = Pattern.compile("(?:\\b|[^a-zA-Z0-9$])([a-zA-Z0-9$]{1,4})" +
            "\\s*=\\s*function\\(\\s*a\\s*\\)\\s*\\{\\s*a\\s*=\\s*a\\.split\\(\\s*\"\"\\s*\\)");
    private static final Pattern patVariableFunction = Pattern.compile("([{; =])([a-zA-Z$][a-zA-Z0-9$]{0,2})" +
            "\\.([a-zA-Z$][a-zA-Z0-9$]{0,2})\\(");
    private static final Pattern patFunction = Pattern.compile("([{; =])([a-zA-Z$_][a-zA-Z0-9$]{0,2})\\(");

    static {
        System.setProperty("nashorn.args","--no-deprecation-warning");
    }

    private SignatureEngine(String decipherJsFileName, String decipherFunctionName, String decipherFunctions) {
        this.mDecipherJsFileName = decipherJsFileName;
        this.mDecipherFunctionName = decipherFunctionName;
        this.mDecipherFunctions = decipherFunctions;
    }

    private static String getDecipherJsFileContent(String decipherJsFileName) throws IOException {
        String decipherJsFileURL = "https://youtube.com" + decipherJsFileName;
        BufferedReader reader = null;
        String javascriptFile = null;
        URL url = new URL(decipherJsFileURL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("User-Agent", USER_AGENT);
        try {
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append(" ");
            }
            javascriptFile = sb.toString();
        } finally {
            if (reader != null)
                reader.close();
            urlConnection.disconnect();
            return javascriptFile;
        }
    }

    private static Map<String, String> getDecipherFunctions(String decipherJsFileContent) throws ParseException {
        String decipherFunctionName = null;
        String decipherFunctions = "";
        Matcher mat = patSignatureDecFunction.matcher(decipherJsFileContent);
        if (mat.find()) {
            decipherFunctionName = mat.group(1);

            Pattern patMainVariable = Pattern.compile("(var |\\s|,|;)"
                    + decipherFunctionName.replace("$", "\\$") +
                    "(=function\\((.{1,3})\\)\\{)");

            String mainDecipherFunct;

            mat = patMainVariable.matcher(decipherJsFileContent);
            if (mat.find()) {
                mainDecipherFunct = "var " + decipherFunctionName + mat.group(2);
            } else {
                Pattern patMainFunction = Pattern.compile("function " +
                        decipherFunctionName.replace("$", "\\$") +
                        "(\\((.{1,3})\\)\\{)");
                mat = patMainFunction.matcher(decipherJsFileContent);
                if (!mat.find())
                    throw new ParseException("Main decipher function not found", 0);

                mainDecipherFunct = "function " + decipherFunctionName + mat.group(2);
            }

            int startIndex = mat.end();

            for (int braces = 1, i = startIndex; i < decipherJsFileContent.length(); i++) {
                if (braces == 0 && startIndex + 5 < i) {
                    mainDecipherFunct += decipherJsFileContent.substring(startIndex, i) + ";";
                    break;
                }
                if (decipherJsFileContent.charAt(i) == '{')
                    braces++;
                else if (decipherJsFileContent.charAt(i) == '}')
                    braces--;
            }
            decipherFunctions = mainDecipherFunct;
            // Search the main function for extra functions and variables
            // needed for deciphering
            // Search for variables
            mat = patVariableFunction.matcher(mainDecipherFunct);
            while (mat.find()) {
                String variableDef = "var " + mat.group(2) + "={";
                if (decipherFunctions.contains(variableDef)) {
                    continue;
                }
                startIndex = decipherJsFileContent.indexOf(variableDef) + variableDef.length();
                for (int braces = 1, i = startIndex; i < decipherJsFileContent.length(); i++) {
                    if (braces == 0) {
                        decipherFunctions += variableDef + decipherJsFileContent.substring(startIndex, i) + ";";
                        break;
                    }
                    if (decipherJsFileContent.charAt(i) == '{')
                        braces++;
                    else if (decipherJsFileContent.charAt(i) == '}')
                        braces--;
                }
            }
            // Search for functions
            mat = patFunction.matcher(mainDecipherFunct);
            while (mat.find()) {
                String functionDef = "function " + mat.group(2) + "(";
                if (decipherFunctions.contains(functionDef)) {
                    continue;
                }
                startIndex = decipherJsFileContent.indexOf(functionDef) + functionDef.length();
                for (int braces = 0, i = startIndex; i < decipherJsFileContent.length(); i++) {
                    if (braces == 0 && startIndex + 5 < i) {
                        decipherFunctions += functionDef + decipherJsFileContent.substring(startIndex, i) + ";";
                        break;
                    }
                    if (decipherJsFileContent.charAt(i) == '{')
                        braces++;
                    else if (decipherJsFileContent.charAt(i) == '}')
                        braces--;
                }
            }

            Map<String, String> result = new HashMap<>();
            result.put("decipherFunctionName", decipherFunctionName);
            result.put("decipherFunctions", decipherFunctions);
            return result;
        } else {
            throw new ParseException("Decipher function signature not found", 0);
        }

    }

    public static SignatureEngine create(String pageHtml) throws ParseException, IOException {
        Matcher mat = patDecryptionJsFile.matcher(pageHtml);
        if (!mat.find())
            mat = patDecryptionJsFileWithoutSlash.matcher(pageHtml);
        if (mat.find()) {
            final String decipherJsFileName = mat.group(0).replace("\\/", "/");

            String decipherJsFileContent = getDecipherJsFileContent(decipherJsFileName);
            Map<String, String> decipherResult = getDecipherFunctions(decipherJsFileContent);

            return new SignatureEngine(
                    decipherJsFileName,
                    decipherResult.get("decipherFunctionName"),
                    decipherResult.get("decipherFunctions"));
        } else {
            throw new ParseException("Decipher JS filename not found", 0);
        }
    }

    public String decipher(String signature) throws ScriptException {
        final StringBuilder decipherJSCode = new StringBuilder(mDecipherFunctions + " function decipher(");
        decipherJSCode.append("){return ");
        decipherJSCode.append(mDecipherFunctionName).append("('").append(signature).append("')");
        decipherJSCode.append("};decipher();");

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        CompiledScript script = ((Compilable) engine).compile(decipherJSCode.toString());

        return String.valueOf(script.eval());
    }

    @Override
    public String toString() {
        return "Decipher{" +
                "decipherJsFileName='" + mDecipherJsFileName + '\'' +
                ", decipherFunctions='" + mDecipherFunctions +
                '}';
    }

}
