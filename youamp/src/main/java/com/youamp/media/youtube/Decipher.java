package com.youamp.media.youtube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Decipher {

    private String mDecipherJsFileName;
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

    private Decipher(String decipherJsFileName, String decipherFunctions) {
        this.mDecipherJsFileName = decipherJsFileName;
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

    private static String getDecipherFunctions(String decipherJsFileContent) throws ParseException {
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

            return decipherFunctions;
        } else {
            throw new ParseException("Decipher function signature not found", 0);
        }

    }

    public static Decipher create(String pageHtml) throws ParseException, IOException {
        Matcher mat = patDecryptionJsFile.matcher(pageHtml);
        if (!mat.find())
            mat = patDecryptionJsFileWithoutSlash.matcher(pageHtml);
        if (mat.find()) {
            final String decipherJsFileName = mat.group(0).replace("\\/", "/");

            String decipherJsFileContent = getDecipherJsFileContent(decipherJsFileName);
            String decipherFunctions = getDecipherFunctions(decipherJsFileContent);

            return new Decipher(decipherJsFileName, decipherFunctions);
        } else {
            throw new ParseException("Decipher JS filename not found", 0);
        }
    }

    @Override
    public String toString() {
        return "Decipher{" +
                "decipherJsFileName='" + mDecipherJsFileName + '\'' +
                ", decipherFunctions='" + mDecipherFunctions +
                '}';
    }

}
