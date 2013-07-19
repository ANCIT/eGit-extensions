package org.ancit.github.utils;

import java.io.IOException;
import java.net.URL;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

public class GithubService {
	
	/**
	 * Create client for url
	 *
	 * @param url
	 * @return client
	 * @throws IOException
	 */
	public static GitHubClient createClient(String url) throws IOException {
		GitHubClient client = null;
		if (url != null) {
			URL parsed = new URL(url);
			client = new GitHubClient(parsed.getHost(), parsed.getPort(),
					parsed.getProtocol());
		} else
			client = new GitHubClient();
		return configure(client);
	}
	
	/**
	 * Configure client
	 *
	 * @param client
	 * @return specified client
	 */
	private static GitHubClient configure(GitHubClient client) {
		
		ISecurePreferences preferences = SecurePreferencesFactory.getDefault();
		if (preferences.nodeExists("eGitUserInfo")) {
			ISecurePreferences node = preferences.node("eGitUserInfo");
			try {
				String user = node.get("eGIT_USERNAME", "n/a");
				String password = node.get("eGIT_PASSWORD", "n/a");
				client.setCredentials(user, password);
				client.setUserAgent(user);
			} catch (StorageException e1) {
				e1.printStackTrace();
			}
		} else {
			client.setUserAgent("github");
		}
		return client;
	}

}
