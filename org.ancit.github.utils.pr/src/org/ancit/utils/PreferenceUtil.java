package org.ancit.utils;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jgit.util.StringUtils;

/***
 *
 * @author luhaixun@gmail.com
 *
 */
public class PreferenceUtil {

	private PreferenceUtil() {
		// TODO Auto-generated constructor stub
	}

	/***
	 *
	 * @return true if it's a GitHub Enterprise Host. Otherwise, return false.
	 */
	public static boolean isGitEnterprise() {
		String gitHost = getGitHost();
		return !StringUtils.isEmptyOrNull(gitHost);
	}

	/***
	 *
	 * @return GitHub Enterprise Host which is configured in the Preference
	 *         Window. If any error happens, return null.
	 */
	public static String getGitHost() {
		ISecurePreferences preferences = SecurePreferencesFactory.getDefault();
		String gitHost = null;
		if (preferences.nodeExists("eGitUserInfo")) {
			ISecurePreferences node = preferences.node("eGitUserInfo");
			try {
				gitHost = node.get("eGIT_HOST", "n/a");
			} catch (StorageException e1) {
				e1.printStackTrace();
			}
		}
		return gitHost;
	}

}
