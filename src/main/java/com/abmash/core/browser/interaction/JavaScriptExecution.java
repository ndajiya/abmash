package com.abmash.core.browser.interaction;


import java.util.ArrayList;

import org.openqa.selenium.JavascriptExecutor;

import com.abmash.api.Browser;
import com.abmash.api.data.JavaScriptResult;
import com.abmash.core.tools.JavaScriptParameterConverter;


public class JavaScriptExecution extends ActionOnBrowser {
	
	private String script = null;
	
	private Boolean sync = true;

	private Object[] args = null;

	private JavaScriptResult result = null;

	public JavaScriptExecution(Browser browser, String script, Boolean sync, Object... args) {
		super(browser);
		this.script = script;
		this.sync = sync;
		this.args = convertArguments(args);
	}
	
	private Object[] convertArguments(Object... args) {
		ArrayList<Object> arguments = new ArrayList<Object>();
		for (Object arg: args) {
			Object argument = new JavaScriptParameterConverter().apply(arg);
			arguments.add(argument);
		}
		return arguments.toArray();
	}
	
	@Override
	protected void perform() {
		browser.log().trace("Executing JavaScript: " + script);
		executeScript();
	}
	
	private void executeScript() {
		try {
			if(sync) {
				result = new JavaScriptResult(executeSyncScript());
			} else {
				result = new JavaScriptResult(executeAsyncScript());
			}
		} catch(Exception e) {
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			String jsMessage = "null";
			try {
//				jsMessage = browser.query().cssSelector("body").findFirst().getAttribute("javaScriptErrorMessage");
			} catch (Exception e2) {
				// TODO error handling?
			}
			String errorMessage = "JavaScript execution failed: " + jsMessage;
			errorMessage += "\n" + e.getMessage();
			errorMessage += "\n >> for the following script:\n" + script;
			errorMessage += "\n >> with the following arguments:\n" + args.toString();
			System.err.println(errorMessage + "\n");
			e.printStackTrace();
		}
		// TODO output
		if(!result.isNull()) browser.log().debug("returnValue class: " + result.getClass());
	}
	
	private Object executeSyncScript() {
		return ((JavascriptExecutor) browser.getWebDriver()).executeScript(script, args);
	}
	
	private Object executeAsyncScript() {
		return ((JavascriptExecutor) browser.getWebDriver()).executeAsyncScript(script, args);
	}
	
	public JavaScriptResult getResult() {
		return result;
	}
	
}
