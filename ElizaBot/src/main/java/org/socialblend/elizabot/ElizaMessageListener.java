package org.socialblend.elizabot;

import net.chayden.eliza.Eliza;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class ElizaMessageListener implements MessageListener {

	private static final Logger LOG = Logger
			.getLogger(ElizaMessageListener.class);
	private final Eliza eliza;

	public ElizaMessageListener(Chat chat) {
		this.eliza = new Eliza();
	}

	public void processMessage(Chat chat, Message message) {
		String input = message.getBody();
		LOG.debug("IN:  " + input);
		String answer = eliza.processInput(input);
		LOG.debug("OUT: "+answer);
		try {
			chat.sendMessage(answer);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

}
