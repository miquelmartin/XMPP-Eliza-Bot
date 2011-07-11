package org.socialblend.elizabot;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Presence.Type;

public class ElizaBot {

	private static final Logger LOG = Logger.getLogger(ElizaBot.class);

	private final Connection connection;

	private final String username;
	private final String password;
	private String resource = "NoResource";

	private Roster roster;
	private ChatManager chatManager;

	public static void main(String[] args) {
		CommandLine cmd = getCommandLine(args);
		String username = cmd.hasOption('u') ? cmd.getOptionValue('u')
				: "SocialBlendEliza";
		String server = cmd.hasOption('s') ? cmd.getOptionValue('s')
				: "jabber.org";
		String password = cmd.hasOption('p') ? cmd.getOptionValue('p')
				: "socblend8834";
		String resource = cmd.hasOption('r') ? cmd.getOptionValue('r')
				: "SomeonesComputer";
		Connection.DEBUG_ENABLED = cmd.hasOption('d');

		ElizaBot bot = new ElizaBot(username, server, password, resource);

		bot.start();
		try {
			LOG.info("Press enter to stop the bot");
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Shutdown on user input at the console
		bot.stop();
		System.exit(0);
	}

	protected static CommandLine getCommandLine(String[] args) {
		Options options = new Options();
		options.addOption("u", true,
				"the username (defaults to SocialBlendEliza)");
		options.addOption("p", true,
				"password for that login name (defaults to socblend8834)");
		options.addOption("s", true,
				"server where the account exists (defaults to jabber.org)");
		options.addOption("r", true,
				"resource you are connecting from (default: SomeonesComputer)");
		options.addOption("d", false, "enable debug (default: false)");
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			printHelp(options);
			System.exit(1);
		}

		if (cmd.hasOption('h')) {
			printHelp(options);
			System.exit(0);
		}
		return cmd;
	}

	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar ElizaBot-bin.jar  [-u username] [-p password] [-s server] [-r resource] [-d] ", options);
	}

	public ElizaBot(String login, String server, String password,
			String resource) {
		this.username = login;
		this.password = password;
		this.resource = resource;
		this.connection = new XMPPConnection(server);
		LOG.debug("Eliza Bot created at " + username + "@" + server + "/"
				+ resource);
	}

	public void start() {
		try {
			connection.connect();
			connection.login(username, password, resource);
			LOG.debug("ElizaBot is online");

			setAvailable(true);

			roster = connection.getRoster();
			roster.setSubscriptionMode(SubscriptionMode.accept_all); // Default

			chatManager = connection.getChatManager();
			chatManager.addChatListener(new ChatManagerListener() {

				public void chatCreated(Chat chat, boolean createdLocally) {
					chat.addMessageListener(new ElizaMessageListener(chat));
				}
			});
		} catch (XMPPException e) {
			throw new RuntimeException("XMPP Error", e);
		}
	}

	public void stop() {
		setAvailable(false);
		LOG.debug("Disconnecting");
		connection.disconnect();
	}

	protected void setAvailable(boolean available) {
		Presence p;
		if (available) {
			p = new Presence(Type.available,
					"Ready to listen to your problems, Yay.", 10, Mode.chat);
		} else {
			p = new Presence(Type.unavailable);
			p.setStatus("Come back during visiting hours!");
		}
		connection.sendPacket(p);
	}
}
