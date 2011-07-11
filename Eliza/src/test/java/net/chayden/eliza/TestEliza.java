package net.chayden.eliza;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestEliza {

	Eliza eliza = null;

	@Before
	public void init() {
		eliza = new Eliza();
	}

	@After
	public void cleanup() {
		eliza = null;
	}

	@Test
	public void testSimpleMessages() {
		String[] inputs = new String[] { "Captain Hook is awesome",
				"I like turtles", "I hate you!", "What's your name?",
				"Piss off!" };
		String[] expectedResponse = new String[] {
				"I'm not sure I understand you fully.",
				"You say you like turtles  ?",
				"Perhaps in your fantasies we hate each other.",
				"I am not interested in names.", "Please go on." };

		for (int i = 0; i < inputs.length; i++) {
			String response = eliza.processInput(inputs[i]);
			System.out.println("Q: " + inputs[i]);
			System.out.println("A: " + response + " (expecting: "
					+ expectedResponse[i] + ")");
			Assert.assertEquals(expectedResponse[i], response);
		}
	}

	@Test
	public void testRepetitiveMessages() {
		String[] inputs = new String[] { "Sorry", "Sorry", "Sorry", "Sorry",
				"Sorry" };
		String[] expectedResponse = new String[] { "Please don't apologise.",
				"Apologies are not necessary.",
				"I've told you that apologies are not required.",
				"It did not bother me.", "Please don't apologise." };

		for (int i = 0; i < inputs.length; i++) {
			String response = eliza.processInput(inputs[i]);
			System.out.println("Q: " + inputs[i]);
			System.out.println("A: " + response + " (expecting: "
					+ expectedResponse[i] + ")");
			// Assert.assertEquals(expectedResponse[i],response);
		}
	}
}
