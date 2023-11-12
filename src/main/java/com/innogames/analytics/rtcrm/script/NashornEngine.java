package com.innogames.analytics.rtcrm.script;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

public class NashornEngine {

	private static NashornEngine nashornEngine;

	private final ScriptEngine engine;

	private NashornEngine() {
		engine = new NashornScriptEngineFactory().getScriptEngine(
			new String[] {"-doe", "-strict"},
			Thread.currentThread().getContextClassLoader(),
			className -> className.startsWith("com.innogames.analytics.rtcrm")
		);
	}

	public Object eval(final String script) throws ScriptException {
		return engine.eval(script);
	}

	public static NashornEngine getInstance() {
		if (nashornEngine == null) {
			nashornEngine = new NashornEngine();
		}

		return nashornEngine;
	}

}
