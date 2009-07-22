/**
 * Copyright 2009 Joe LaPenna
 */

package com.joelapenna.foursquared;

import com.joelapenna.foursquare.Foursquare;
import com.joelapenna.foursquare.error.FoursquareCredentialsError;
import com.joelapenna.foursquare.error.FoursquareException;
import com.joelapenna.foursquare.types.Credentials;
import com.joelapenna.foursquare.types.classic.Auth;
import com.joelapenna.foursquare.types.classic.User;

import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.io.IOException;

/**
 * @author Joe LaPenna (joe@joelapenna.com)
 */
public class Preferences {
    private static final String TAG = "Preferences";
    private static final boolean DEBUG = Foursquared.DEBUG;

    public static final String PREFERENCE_PHONE = "phone";
    public static final String PREFERENCE_PASSWORD = "password";
    public static final String PREFERENCE_TWITTER_CHECKIN = "twitter_checkin";
    public static final String PREFERENCE_SILENT_CHECKIN = "silent_checkin";

    // Not-in-XML preferences
    public static final String PREFERENCE_CITY_ID = "city_id";
    public static final String PREFERENCE_EMAIL = "email";
    public static final String PREFERENCE_FIRST = "first_name";
    public static final String PREFERENCE_GENDER = "gender";
    public static final String PREFERENCE_ID = "id";
    public static final String PREFERENCE_LAST = "last_name";
    public static final String PREFERENCE_PHOTO = "photo";

    // Not-in-XML preferences for oAuth
    public static final String PREFERENCE_OAUTH_TOKEN = "oauth_token";
    public static final String PREFERENCE_OAUTH_TOKEN_SECRET = "oauth_token_secret";

    static void storeUser(final Editor editor, User user) {
        if (user != null && user.getId() != null) {
            editor.putString(PREFERENCE_CITY_ID, user.getCityid());
            editor.putString(PREFERENCE_ID, user.getId());
            editor.putString(PREFERENCE_GENDER, user.getGender());
            if (DEBUG) Log.d(TAG, "Commiting user info: " + String.valueOf(editor.commit()));
        } else {
            if (PreferenceActivity.DEBUG) Log.d(PreferenceActivity.TAG, "Unable to lookup user.");
        }
    }

    static final void storeLoginAuth(Editor editor, Auth auth) throws FoursquareCredentialsError {
        if (auth != null && auth.status() /* && user != null */) {
            editor.putString(PREFERENCE_EMAIL, auth.getEmail());
            editor.putString(PREFERENCE_FIRST, auth.getFirstname());
            editor.putString(PREFERENCE_LAST, auth.getLastname());
            editor.putString(PREFERENCE_PHOTO, auth.getPhoto());
            if (DEBUG) Log.d(TAG, "Commiting auth info: " + String.valueOf(editor.commit()));
        } else {
            throw new FoursquareCredentialsError("Unable to login.");
        }
    }

    static void storeAuthExchangeCredentials(final Editor editor, Credentials credentials)
            throws FoursquareCredentialsError {
        if (credentials != null && credentials.getOauthToken() != null
                && credentials.getOauthTokenSecret() != null) {
            if (DEBUG) Log.d(TAG, "Storing oauth token");
            editor.putString(PREFERENCE_OAUTH_TOKEN, credentials.getOauthToken());
            editor.putString(PREFERENCE_OAUTH_TOKEN_SECRET, credentials.getOauthTokenSecret());
            if (DEBUG) Log.d(TAG, "Commiting authexchange token: " + String.valueOf(editor.commit()));
        } else {
            throw new FoursquareCredentialsError("Unable to auth exchange.");
        }
    }

    static void loginUser(final Editor editor, final Foursquare foursquare,
            final boolean doAuthExchange) throws FoursquareCredentialsError, FoursquareException,
            IOException {
        if (PreferenceActivity.DEBUG) Log.d(PreferenceActivity.TAG, "Trying to log in.");
        Auth auth = foursquare.login();
        Preferences.storeLoginAuth(editor, auth);

        User user = foursquare.user();
        Preferences.storeUser(editor, user);

        if (doAuthExchange) {
            if (DEBUG) Log.d(TAG, "doAuthExchange specified for loginUser");
            Credentials credentials = foursquare.authExchange();
            Preferences.storeAuthExchangeCredentials(editor, credentials);
        }
    }

}
