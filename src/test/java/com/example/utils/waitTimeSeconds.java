 package com.example.utils;

import java.util.concurrent.TimeUnit;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class waitTimeSeconds implements Task{
	
	private final String value;
	private final int timeout;
	
	public  static Performable on(String value, int timeout) {
		return instrumented(waitTimeSeconds.class, value, timeout);
	}
	
	public waitTimeSeconds(String value, int timeout) {
		this.value = value;
		this.timeout = timeout;
	}

	@Override
	@Step("{0} Waiting for #timeout")
	public <T extends Actor> void performAs(T actor) {
		// TODO Auto-generated method stub
		
		try{
			TimeUnit.SECONDS.sleep(timeout);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
