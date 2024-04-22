import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
public class JTPRE {
	int op;
	Scanner in = new Scanner(System.in);
	public JTPRE() throws IOException {
		input();
	}
	public void input() throws IOException {
		Scanner in = new Scanner(System.in);
		int numLBrackets = 0;
		int numRBrackets = 0;
		do {
			System.out.println("Enter 1 for java file or 0 to end");
			int input = in.nextInt();
			op = input;
			if(op == 1) {
				String str;
				Scanner inp = new Scanner(System.in);
				System.out.println("Enter file name followed by .txt");
				String fileName = inp.next();
				//System.out.println(correctSemantics(fileName));
				toPython(fileName);
				 }
			else if(op == 0) {
				break;
			}
			else {
				System.out.println("Input must be 1 or 0");
			}
		}while(op != 0);
	}
	public String toPython(String fileName) throws IOException {
		String python = "";
		correctSemantics(fileName);
		checkBrackets(fileName);
		try {
	        FileReader fileReader = new FileReader(fileName);
	        BufferedReader bufferedReader = new BufferedReader(fileReader);
	        String line;
	       while ((line = bufferedReader.readLine()) != null) {
		if(correctSyntax(line) == false) {
			System.out.println("Incorrect Syntax");
			System.out.println(line);
		}
	       }
		}catch (IOException e) {
	           System.out.println("An error occurred while reading the file: " + e.getMessage());  
		}
		return python;
	}
	public String Expected() {
		return "";
	}
	public boolean correctSyntax(String line) {
		for(int i =0; i < line.length(); i++) {
			if(line.contains("System.out.println(")) {
				String pattern = "System\\.out\\.(println|print)\\(\\s*\"(.*?)\"(\\s*\\+\\s*\\w+)*\\s*\\);";
				String pattern2 = "\\s*System\\.out\\.(println|print)\\(\\s*\\w+\\s*\\)\\s*;";
	        if(line.matches(pattern) == false && line.matches(pattern2) == false) {
	        	return false;
	        }
	        else {
	        	return true;
	        }
		}
	        if(line.matches("for\\s*\\(.*?;.*?;.*?\\)\\s*\\{?")) {
	        	return true;
	        }
	        if(line.matches(".*?class\\s+\\w+\\s*\\{?")) {
	        	return true;
			}
				if(line.matches("\\s*(public|protected|private|static|final|abstract|synchronized|native|strictfp)?\\s*(void|int|double|float|long|short|byte|char|boolean)\\s+\\w+\\s*\\(.*?\\)\\s*\\{?")) {
			return true;
			}
	        if(line.matches("\\s*public\\s+static\\s+void\\s+main\\s*\\(\\s*String\\s*\\[\\s*\\]\\s*args\\s*\\)\\s*\\{?")) {
	        	return true;
			}
			//empty
			if(line.matches("^$")) {
				return true;
			}
			//closing bracket
			if(line.matches("\\}$")) {
				return true;
			}
			//variable
			if(line.matches("\\s*\\w+\\s+\\w+\\s*=\\s*.*?;")) {
				if(line.contains("String")) {
					if(line.matches("\\bString\\s+[a-zA-Z_$][a-zA-Z_$0-9]*\\s*=\\s*\".*\"\\s*;") == false) {
						return false;
					}
				}
				if(line.contains("char")) {
					if(line.matches("\\bchar\\s+[a-zA-Z_$][a-zA-Z_$0-9]*\\s*=\\s*'.*'\\s*;") == false) {
						return false;
					}
				}
				return true;
			}
			//if
			if(line.matches("\\s*if\\s*\\(.*?(<|>|==|&&|\\|\\|).*?\\)\\s*\\{")) {
				return true;
			}
			//array
			if(line.matches("\\s*\\w+\\s*\\[.*?\\]\\s*\\w+\\s*=\\s*new\\s+\\w+\\s*\\[.*?\\]\\s*;")) {
				return true;
			}
			//while
			if(line.matches("\\s*while\\s*\\(.*?\\)\\s*\\{")) {
				return true;
			}
			//math
			if(line.matches("\\s*\\d+\\s*[+\\-*/]\\s*\\d+\\s*")) {
				return true;
			}
			//variable math
			if(line.matches("\\b[a-zA-Z_$][a-zA-Z_$0-9]*\\s*=\\s*[\\w+\\-*/()]+\\s*;")) {
				return true;
			}
		}
		return false;
	}

  private boolean correctSemantics(String filename) throws IOException {
      Map<String, String> variableTypes = new HashMap<>();
      Set<String> variablesInMathOperations = new HashSet<>();
      try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
          String line;
          while ((line = br.readLine()) != null) {
              // Regular expression to match variable declarations
              Matcher matcher = Pattern.compile("\\b(\\w+)\\b\\s+(\\w+)\\s*=.*;").matcher(line);
              if (matcher.find()) {
                  String variableType = matcher.group(1);
                  String variableName = matcher.group(2);
                  variableTypes.put(variableName, variableType);
              }
          }
      }
      try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
          String line;
          while ((line = br.readLine()) != null) {
              Matcher matcher = Pattern.compile("\\b\\w+(\\.\\d+)?(?:\\d+)?\\b").matcher(line); // Match word boundaries
              while (matcher.find()) {
                  String variableName = matcher.group();
                  if (line.contains("+") || line.contains("-") || line.contains("*") || line.contains("/") || line.matches("\\s*\\w+\\s*=\\s*.*")) {
                      // If the line contains a mathematical operation, add the variable to the set
                      variablesInMathOperations.add(variableName);
                  }
              }
          }
      }
          String commonType = null;
          for (String variable : variablesInMathOperations) {
              String type = variableTypes.get(variable);
              if(variable.matches("\\d+")){
            	  type = "int";
              }
              if(variable.matches("-?\\d+(\\.\\d+)")){
            	  type = "double";
              }
              if (commonType == null) {
                  commonType = type;
              } else if (!commonType.equals(type)) {
            	  System.out.println("Incorrect Semantics");
                  return false; // Different types found
              }
          }
      return true; 
  }
public boolean checkBrackets(String fileName){
	Deque<Character> stack = new ArrayDeque<Character>();
	try {
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
       while ((line = bufferedReader.readLine()) != null) {
    	   for (char c : line.toCharArray()) {
    		   if (c == '(' || c == '[' || c == '{') {
                   stack.push(c);
               } else if (c == ')' || c == ']' || c == '}') {
                   if (stack.isEmpty()) {
                       return false; 
                   }
                   char openingBracket = stack.pop();
                   if ((c == ')' && openingBracket != '(') ||
                       (c == ']' && openingBracket != '[') ||
                       (c == '}' && openingBracket != '{')) {
                       return false;
                   }
               }  
       }
       }
        bufferedReader.close();
 }catch (IOException e) {
        System.out.println("An error occurred while reading the file: " + e.getMessage());  
}
	if(stack.isEmpty() == false) {
		System.out.println("Bracket Imbalance");
	}
	 return stack.isEmpty();
}
}