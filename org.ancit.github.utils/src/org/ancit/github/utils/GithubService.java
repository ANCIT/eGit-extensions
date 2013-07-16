package org.ancit.github.utils;

import java.io.IOException;
import java.net.URL;

import org.eclipse.egit.github.core.client.GitHubClient;

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
		client.setUserAgent("github");
		return client;
	}

}
