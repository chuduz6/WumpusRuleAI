import java.io.*;
import java.util.*;

/**
 * @author james spargo
 *Edited by Chudamani Aryal
 */
public class CheckTrueFalse {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if( args.length != 3){
			//takes three arguments
			System.out.println("Usage: " + args[0] +  " [wumpus-rules-file] [additional-knowledge-file] [input_file]\n");
			exit_function(0);
		}
		
		//create some buffered IO streams
		String buffer;
		BufferedReader inputStream;
		BufferedWriter outputStream;
		
		//create the knowledge base and the statement
		LogicalExpression knowledge_base = new LogicalExpression();
		LogicalExpression statement = new LogicalExpression();

		//open the wumpus_rules.txt
		try {
			inputStream = new BufferedReader( new FileReader( args[0] ) );
			
			//load the wumpus rules
			System.out.println("loading the wumpus rules...");
			knowledge_base.setConnective("and");
		
			while(  ( buffer = inputStream.readLine() ) != null ) 
                        {
				if( !(buffer.startsWith("#") || (buffer.equals( "" )) )) 
                                {
					//the line is not a comment
					LogicalExpression subExpression = readExpression( buffer );
					knowledge_base.setSubexpression( subExpression );
				} 
                                else 
                                {
					//the line is a comment. do nothing and read the next line
				}
			}		
			
			//close the input file
			inputStream.close();

		} catch(Exception e) 
                {
			System.out.println("failed to open " + args[0] );
			e.printStackTrace();
			exit_function(0);
		}
		//end reading wumpus rules
		
		
		//read the additional knowledge file
		try {
			inputStream = new BufferedReader( new FileReader( args[1] ) );
			
			//load the additional knowledge
			System.out.println("loading the additional knowledge...");
			
			// the connective for knowledge_base is already set.  no need to set it again.
			// i might want the LogicalExpression.setConnective() method to check for that
			//knowledge_base.setConnective("and");
			
			while(  ( buffer = inputStream.readLine() ) != null) 
                        {
                                if( !(buffer.startsWith("#") || (buffer.equals("") ))) 
                                {
					LogicalExpression subExpression = readExpression( buffer );
					knowledge_base.setSubexpression( subExpression );
                                } 
                                else 
                                {
				//the line is a comment. do nothing and read the next line
                                }
                          }
			
			//close the input file
			inputStream.close();

		} catch(Exception e) {
			System.out.println("failed to open " + args[1] );
			e.printStackTrace();
			exit_function(0);
		}
		//end reading additional knowledge
		
		
		// check for a valid knowledge_base
		if( !valid_expression( knowledge_base ) ) {
			System.out.println("invalid knowledge base");
			exit_function(0);
		}
		
		// print the knowledge_base
		//knowledge_base.print_expression("\n");
		
		
		// read the statement file
		try {
			inputStream = new BufferedReader( new FileReader( args[2] ) );
			
			System.out.println("\n\nLoading the statement file...");
			//buffer = inputStream.readLine();
			
			// actually read the statement file
			// assuming that the statement file is only one line long
			while( ( buffer = inputStream.readLine() ) != null ) {
				if( !buffer.startsWith("#") ) {
					    //the line is not a comment
						statement = readExpression( buffer );
                                                break;
				} else {
					//the line is a commend. no nothing and read the next line
				}
			}
			
			//close the input file
			inputStream.close();

		} catch(Exception e) {
			System.out.println("failed to open " + args[2] );
			e.printStackTrace();
			exit_function(0);
		}
		// end reading the statement file
		
		// check for a valid statement
		if( !valid_expression( statement ) ) {
			System.out.println("invalid statement");
			exit_function(0);
		}
		
		//print the statement
		//statement.print_expression( "" );
		//print a new line
		System.out.println("\n");
						
		//testing 
		//we print the result here 
		printStatementResult(knowledge_base, statement);
		System.out.println("\nThe result is saved on result.txt file as well.");
		

	} //end of main
	
	// Given the function of entailement, this function decides whether the statement in the file in true or false, or unknown
	// returns true iff the statement is definitely true given kb, false iff the statement is definitely false given kb, and unsure (both true and false), (Possibly True Possibly False)  otherwise.
	// KB entails alpha 
			// if KB is true alpha is true
			// if KB is true and not alpha is false
		// KB doesn't entail alpha
			// if KB is true and alpha is false
			// and if KB is true and not alpha is true
	
	public static void printStatementResult(LogicalExpression knowledge_base, LogicalExpression statement)
	{
		LogicalExpression notStatement = new LogicalExpression();
		notStatement.setSubexpression(statement);
		notStatement.setConnective("not");	
		Boolean a = ttEntails(knowledge_base, statement);
		Boolean b = ttEntails(knowledge_base, notStatement);
		//System.out.println("\nA bool " + a);
		//System.out.println("B bool " + b);

		
		if(a && b)
		{				
			System.out.println("\nBoth true and false");
				printToFile("result.txt", "Both true and false");
		}
		else if (a && !b)
		{
			System.out.println("\nDefinitely True");
			printToFile("result.txt", "Definitely True");
		}			
		
		else if (!a && b )
		{
			System.out.println("\nDefinitely False");
			printToFile("result.txt", "Definitely False");
		}
		else
		{
			System.out.println("\nPossibly True, Possibly False");
			printToFile("result.txt", "Possibly True, Possibly False");

		}	
	}
	
	// this function just writes the result to an output file
	public static void printToFile( String outputFile, String word ) {
		try {
		    BufferedWriter output = new BufferedWriter(
							       new FileWriter( outputFile ) );
	            output.write(word);


		    
		    //write the current turn
		    output.close();
		    
		} catch( IOException e ) {
		    System.out.println("\nProblem writing to the output file!\n" +
				       "Try again.");
		    e.printStackTrace();
		}
	    }
	
	// the purpose of this function is to extract symbols from the knowledge base as well as statement which is known as alpha over here.
	// this function calls the another extract symbol function where the main extraction takes place
	// and then combines those extracted symbols
	// and then removes the duplicates from the list
	public static List<String> extract_Symbols(LogicalExpression kb, LogicalExpression alpha)
	{
		List<String> extractedSymbols = new Vector<String>(parse(kb)); 
		extractedSymbols.addAll(parse(alpha)); 
		List<String> extractedSymbolsNoDuplicates = new Vector<String>(new HashSet<String>(extractedSymbols));
		return extractedSymbolsNoDuplicates;
	}
	 
	// this is the function where the main extraction takes place
	// using the for loop for the subexpressions, it gets and adds all the symbols in a list and returns the list to the function where is was called from
	// the base case would be when the statement only has the symbol as a logical expression. 
	// The recursive case would be when the statement is not completely broken down into symbol i.e when the statement is not a symbol yet.
	private static List<String> parse(LogicalExpression list)
	{

		List<String> listOfSymbols = new Vector<String>();
		if ( (list.getUniqueSymbol() != null) )
		{
				listOfSymbols.add(list.getUniqueSymbol());
			  	return listOfSymbols;
		}
		else
		{
			for( Enumeration enumn = list.getSubexpressions().elements(); enumn.hasMoreElements(); )
			{
				listOfSymbols.addAll( parse((LogicalExpression)enumn.nextElement()) );
			}
		}
		return listOfSymbols;
	}
	
	
	// this function returns true iff kb (the knowledge base) entails the statement (a logical sentence) and false otherwise
	// first thing it does is extracts all the symbols and collect them in a list
	// then calls the appropriate function for entailment
	
	public static Boolean ttEntails(LogicalExpression kb, LogicalExpression alpha)
	{
		List<String> listOfSymbols = extract_Symbols(kb, alpha);
		Boolean finalTTResult = ttCheckAll(kb, alpha, listOfSymbols, new HashMap<String,Boolean>());
		return finalTTResult;
	}
	
	// this is the main entailment function
	// Any propositional logic statements consist of symbols and connective. 
	// Some only have symbols i.e literal. 
	// In our function the base case is when the statement only has symbols i.e literal. 
	// And the recursive is when statement has connective. 
	// This is because the propositional logic first has to find value of the each symbol and then find the value of the statement including that connective.
	// return true if kb and alpha belongs to that model
	// if kb doesn't belong to the model, then there is no way to check whether the alpha belongs to that model. Hence we assume it's true. 
	
	public static Boolean ttCheckAll(LogicalExpression kb, LogicalExpression alpha, List<String> listOfSymbols, HashMap<String, Boolean> model)
	{
		// base case
		if(listOfSymbols.isEmpty())
		{
			Boolean kbFlag=true;
			Boolean alphaFlag=false;
			alphaFlag = isModelTrue(alpha, model);
			kbFlag = isModelTrue(kb,model);			
			if(kbFlag && !alphaFlag)
			{
				return false;
			}
			else
			{
				return true;
			}

		}
		//recursive case
		else
		{
			
			// first element from the list
			String first = listOfSymbols.get(0);
			// rest of the elements from the list
			List<String> rest = listOfSymbols.subList(1, listOfSymbols.size());
			
			// creating model to extend
			HashMap<String, Boolean> trueModel=new HashMap<String,Boolean>();
			HashMap<String, Boolean> falseModel=new HashMap<String,Boolean>();
			
			// where we extend our model
			trueModel.putAll(model);
			trueModel.put(first, true);
			
			falseModel.putAll(model);
			falseModel.put(first, false);
			

			return ((ttCheckAll(kb, alpha, rest, trueModel)) && (ttCheckAll(kb, alpha, rest, falseModel) ));

		}
	}
	
	// this function checks whether kb or alpha belongs to the particular model or not
	// the base case is when the statement has uniqueSymbol value (which is not null). This means the statement is a symbol
	// the recursive case is when the statement is not completely broken down into symbol
	// In the recursive case, we first set up the truth table and add all the boolean values to the truth table
	// then we call to compare the truth table
	private static Boolean isModelTrue(LogicalExpression statement, HashMap<String, Boolean> model)
	{
		Boolean modelResult = false;	

		if ( !(statement.getUniqueSymbol() == null) )
		{
			modelResult = model.get(statement.getUniqueSymbol());	  	

		}
		else
		{
			Vector<Boolean> truthTable = new Vector<Boolean>();
			String connective = statement.getConnective().toUpperCase();
			for( Enumeration e = statement.getSubexpressions().elements(); e.hasMoreElements(); )
			{
				Boolean newTruthValue = isModelTrue((LogicalExpression)e.nextElement(), model);
				truthTable.add( newTruthValue );
			}

			// this necessary for the omega because omega don't have switch statement for strings
			int x = convertStringToInt(connective);
			
			// comparing the truth table
			modelResult = compareTruthTable(truthTable, x);
		}
		return modelResult;
	}
		
	// this function compares the truth table
	// this is the main function which checks to see if the alpha holds within the model or not.
	public static Boolean compareTruthTable ( Vector<Boolean> truthTable, int a)
	{
		Boolean result =false;
		switch(a)
		{
			case 1:		
				// A implies B
				//IF For example A implies B
				// then !A or B
				if(truthTable.size()!=2)
				{
					exit_function(1);
				}
				
				Boolean A = truthTable.elementAt(0);
				Boolean B = truthTable.elementAt(1);
				if(!A || B)
				{ 
					result = true;
				}
				else
				{
					result = false;
				}
				break;
	
			case 2:	//IFF A1 is equivalent to B1
				// then A1 implies B1 and B1 implies A1
				// A1 implies B1 means !A1 or B1
				// B1 implies A1 means !B1 or A1
				// combined we have (!A1 or B1) and (!B1 or A1)
				// distributing or over and 
				// we have (A1 and B1) or (!(A1 or B1))
				// this means both side implication is true only either both are true or both are false
				if(truthTable.size()!=2)
				{
					exit_function(1);
				}
				Boolean A1 = truthTable.elementAt(0);
				Boolean B1 = truthTable.elementAt(1);
				
				if((A1 && B1) || (!(A1 || B1)))
				{
					result = true;	
				}
				else
				{
					result = false;
				}
				break;
			case 3: 
				// AND
				// here single false will lead to whole statement to be false
				// default is true because (and) is true
				result = true; 
				for(Boolean value : truthTable)
				{
					if(value == false)
					{
						result = false;
						break;
					}
					
				}
				break;

			case 4:	
				//OR
				// single true will lead to whole statement to be true
				// default is false because (or) is false
				result = false;
				for(Boolean value : truthTable)
				{
					if(value == true)
					{
						result = true;	
						break;
					}
					
				}
				break;

			

			case 5:		
				//NOT
				// only takes one agrument, if not then exits from the system
				// if only one agrument, then flip the boolean bit, i.e from true to false and vice versa
				if(truthTable.size()!=1)
				{
					exit_function(1);
				}
				else
				{
					Boolean Anot= truthTable.firstElement();
					result=!Anot;
					
				}					
				break;

			case 6:	
				//XOR	
				// the statement is true only if statement has exactly one true symbol
				// counter is set, if true final counter is 1, return true else return false
				// default is false because (xor) is false
				result = false;
				int counter=0;
				for(Boolean value : truthTable)
				{
					if(value == true)
					{							
						result = true;
						counter=counter+1;
						if(counter>=2)
						{
							result=false;
							break;
						}
						
					}
				}
				break;

			default:
				System.out.println("ERROR: connective not recognized!");
				exit_function(1);
		}	
	return result;
	}
	
	public static int convertStringToInt(String connective)
	{
		int x;
		if(connective.equalsIgnoreCase("IF"))
		{
			x = 1;
		}
		else if(connective.equalsIgnoreCase("IFF"))
		{
			x = 2;
		}
		else if(connective.equalsIgnoreCase("AND"))
		{
			x = 3;
		}
		else if(connective.equalsIgnoreCase("OR"))
		{
			x = 4;
		}
		else if(connective.equalsIgnoreCase("NOT"))
		{
			x = 5;
		}
		else if(connective.equalsIgnoreCase("XOR"))
		{
			x = 6;
		}
		else
		{
			x = 7;
		}
		return x;
	}

	

	/* this method reads logical expressions
	 * if the next string is a:
	 * - '(' => then the next 'symbol' is a subexpression
	 * - else => it must be a unique_symbol
	 * 
	 * it returns a logical expression
	 * 
	 * notes: i'm not sure that I need the counter
	 * 
	 */
	public static LogicalExpression readExpression( String input_string ) 
        {
          LogicalExpression result = new LogicalExpression();
          
          //testing
          //System.out.println("readExpression() beginning -"+ input_string +"-");
          //testing
          //System.out.println("\nread_exp");
          
          //trim the whitespace off
          input_string = input_string.trim();
          
          if( input_string.startsWith("(") ) 
          {
            //its a subexpression
          
            String symbolString = "";
            
            // remove the '(' from the input string
            symbolString = input_string.substring( 1 );
            //symbolString.trim();
            
            //testing
            //System.out.println("readExpression() without opening paren -"+ symbolString + "-");
				  
            if( !symbolString.endsWith(")" ) ) 
            {
              // missing the closing paren - invalid expression
              System.out.println("missing ')' !!! - invalid expression! - readExpression():-" + symbolString );
              exit_function(0);
              
            }
            else 
            {
              //remove the last ')'
              //it should be at the end
              symbolString = symbolString.substring( 0 , ( symbolString.length() - 1 ) );
              symbolString.trim();
              
              //testing
              //System.out.println("readExpression() without closing paren -"+ symbolString + "-");
              
              // read the connective into the result LogicalExpression object					  
              symbolString = result.setConnective( symbolString );
              
              //testing
              //System.out.println("added connective:-" + result.getConnective() + "-: here is the string that is left -" + symbolString + "-:");
              //System.out.println("added connective:->" + result.getConnective() + "<-");
            }
            
            //read the subexpressions into a vector and call setSubExpressions( Vector );
            result.setSubexpressions( read_subexpressions( symbolString ) );
            
          } 
          else 
          {   	
            // the next symbol must be a unique symbol
            // if the unique symbol is not valid, the setUniqueSymbol will tell us.
            result.setUniqueSymbol( input_string );
          
            //testing
            //System.out.println(" added:-" + input_string + "-:as a unique symbol: readExpression()" );
          }
          
          return result;
        }

	/* this method reads in all of the unique symbols of a subexpression
	 * the only place it is called is by read_expression(String, long)(( the only read_expression that actually does something ));
	 * 
	 * each string is EITHER:
	 * - a unique Symbol
	 * - a subexpression
	 * - Delineated by spaces, and paren pairs
	 * 
	 * it returns a vector of logicalExpressions
	 * 
	 * 
	 */
	
	public static Vector<LogicalExpression> read_subexpressions( String input_string ) {

	Vector<LogicalExpression> symbolList = new Vector<LogicalExpression>();
	LogicalExpression newExpression;// = new LogicalExpression();
	String newSymbol = new String();
	
	//testing
	//System.out.println("reading subexpressions! beginning-" + input_string +"-:");
	//System.out.println("\nread_sub");

	input_string.trim();

	while( input_string.length() > 0 ) {
		
		newExpression = new LogicalExpression();
		
		//testing
		//System.out.println("read subexpression() entered while with input_string.length ->" + input_string.length() +"<-");

		if( input_string.startsWith( "(" ) ) {
			//its a subexpression.
			// have readExpression parse it into a LogicalExpression object

			//testing
			//System.out.println("read_subexpression() entered if with: ->" + input_string + "<-");
			
			// find the matching ')'
			int parenCounter = 1;
			int matchingIndex = 1;
			while( ( parenCounter > 0 ) && ( matchingIndex < input_string.length() ) ) {
					if( input_string.charAt( matchingIndex ) == '(') {
						parenCounter++;
					} else if( input_string.charAt( matchingIndex ) == ')') {
						parenCounter--;
					}
				matchingIndex++;
			}
			
			// read untill the matching ')' into a new string
			newSymbol = input_string.substring( 0, matchingIndex );
			
			//testing
			//System.out.println( "-----read_subExpression() - calling readExpression with: ->" + newSymbol + "<- matchingIndex is ->" + matchingIndex );

			// pass that string to readExpression,
			newExpression = readExpression( newSymbol );

			// add the LogicalExpression that it returns to the vector symbolList
			symbolList.add( newExpression );

			// trim the logicalExpression from the input_string for further processing
			input_string = input_string.substring( newSymbol.length(), input_string.length() );

		} else {
			//its a unique symbol ( if its not, setUniqueSymbol() will tell us )

			// I only want the first symbol, so, create a LogicalExpression object and
			// add the object to the vector
			
			if( input_string.contains( " " ) ) {
				//remove the first string from the string
				newSymbol = input_string.substring( 0, input_string.indexOf( " " ) );
				input_string = input_string.substring( (newSymbol.length() + 1), input_string.length() );
				
				//testing
				//System.out.println( "read_subExpression: i just read ->" + newSymbol + "<- and i have left ->" + input_string +"<-" );
			} else {
				newSymbol = input_string;
				input_string = "";
			}
			
			//testing
			//System.out.println( "readSubExpressions() - trying to add -" + newSymbol + "- as a unique symbol with ->" + input_string + "<- left" );
			
			newExpression.setUniqueSymbol( newSymbol );
			
	    	//testing
	    	//System.out.println("readSubexpression(): added:-" + newSymbol + "-:as a unique symbol. adding it to the vector" );

			symbolList.add( newExpression );
			
			//testing
			//System.out.println("read_subexpression() - after adding: ->" + newSymbol + "<- i have left ->"+ input_string + "<-");
			
		}
		
		//testing
		//System.out.println("read_subExpression() - left to parse ->" + input_string + "<-beforeTrim end of while");
		
		input_string.trim();
		
		if( input_string.startsWith( " " )) {
			//remove the leading whitespace
			input_string = input_string.substring(1);
		}
		
		//testing
		//System.out.println("read_subExpression() - left to parse ->" + input_string + "<-afterTrim with string length-" + input_string.length() + "<- end of while");
	}
	return symbolList;
}


	/* this method checks to see if a logical expression is valid or not 
	 * a valid expression either:
	 * ( this is an XOR )
	 * - is a unique_symbol
	 * - has:
	 *  -- a connective
	 *  -- a vector of logical expressions
	 *  
	 * */
	public static boolean valid_expression(LogicalExpression expression)
	{
		
		// checks for an empty symbol
		// if symbol is not empty, check the symbol and
		// return the truthiness of the validity of that symbol

		if ( !(expression.getUniqueSymbol() == null) && ( expression.getConnective() == null ) ) {
			// we have a unique symbol, check to see if its valid
			return valid_symbol( expression.getUniqueSymbol() );

			//testing
			//System.out.println("valid_expression method: symbol is not empty!\n");
			}

		// symbol is empty, so
		// check to make sure the connective is valid
	  
		// check for 'if / iff'
		if ( ( expression.getConnective().equalsIgnoreCase("if") )  ||
		      ( expression.getConnective().equalsIgnoreCase("iff") ) ) {
			
			// the connective is either 'if' or 'iff' - so check the number of connectives
			if (expression.getSubexpressions().size() != 2) {
				System.out.println("error: connective \"" + expression.getConnective() +
						"\" with " + expression.getSubexpressions().size() + " arguments\n" );
				return false;
				}
			}
		// end 'if / iff' check
	  
		// check for 'not'
		else   if ( expression.getConnective().equalsIgnoreCase("not") ) {
			// the connective is NOT - there can be only one symbol / subexpression
			if ( expression.getSubexpressions().size() != 1)
			{
				System.out.println("error: connective \""+ expression.getConnective() + "\" with "+ expression.getSubexpressions().size() +" arguments\n" ); 
				return false;
				}
			}
		// end check for 'not'
		
		// check for 'and / or / xor'
		else if ( ( !expression.getConnective().equalsIgnoreCase("and") )  &&
				( !expression.getConnective().equalsIgnoreCase( "or" ) )  &&
				( !expression.getConnective().equalsIgnoreCase("xor" ) ) ) {
			System.out.println("error: unknown connective " + expression.getConnective() + "\n" );
			return false;
			}
		// end check for 'and / or / not'
		// end connective check

	  
		// checks for validity of the logical_expression 'symbols' that go with the connective
		for( Enumeration e = expression.getSubexpressions().elements(); e.hasMoreElements(); ) {
			LogicalExpression testExpression = (LogicalExpression)e.nextElement();
			
			// for each subExpression in expression,
			//check to see if the subexpression is valid
			if( !valid_expression( testExpression ) ) {
				return false;
			}
		}

		//testing
		//System.out.println("The expression is valid");
		
		// if the method made it here, the expression must be valid
		return true;
	}
	



	/** this function checks to see if a unique symbol is valid */
	//////////////////// this function should be done and complete
	// originally returned a data type of long.
	// I think this needs to return true /false
	//public long valid_symbol( String symbol ) {
	public static boolean valid_symbol( String symbol ) {
		if (  symbol == null || ( symbol.length() == 0 )) {
			
			//testing
			//System.out.println("String: " + symbol + " is invalid! Symbol is either Null or the length is zero!\n");
			
			return false;
		}

		for ( int counter = 0; counter < symbol.length(); counter++ ) {
			if ( (symbol.charAt( counter ) != '_') &&
					( !Character.isLetterOrDigit( symbol.charAt( counter ) ) ) ) {
				
				System.out.println("String: " + symbol + " is invalid! Offending character:---" + symbol.charAt( counter ) + "---\n");
				
				return false;
			}
		}
		
		// the characters of the symbol string are either a letter or a digit or an underscore,
		//return true
		return true;
	}

        private static void exit_function(int value) {
                System.out.println("exiting from checkTrueFalse");
                  System.exit(value);
                }	
}
