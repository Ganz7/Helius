package in.ganz7.heliusweather;


import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;

public class AppPreferences extends PreferenceActivity {
	 
    
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceCategory mCategory = (PreferenceCategory) findPreference("catTwo");
        EditTextPreference pref1 = (EditTextPreference) findPreference("lastUpdate");
        EditTextPreference pref2 = (EditTextPreference) findPreference("updateOrder");
        EditTextPreference pref3 = (EditTextPreference) findPreference("temp_C");
        EditTextPreference pref4 = (EditTextPreference) findPreference("temp_F");
        EditTextPreference pref5 = (EditTextPreference) findPreference("condition");
        EditTextPreference pref6 = (EditTextPreference) findPreference("code");
        EditTextPreference pref7 = (EditTextPreference) findPreference("maxPredictionC");
        EditTextPreference pref8 = (EditTextPreference) findPreference("minPredictionC");
        EditTextPreference pref9 = (EditTextPreference) findPreference("codePredictions");
        EditTextPreference pref10 = (EditTextPreference) findPreference("bgColor");
        EditTextPreference pref11 = (EditTextPreference) findPreference("txtColor");
        
        /*
         * Hides the preferences from showing up on the Preferences Activity
         */
        mCategory.removePreference(pref1);
        mCategory.removePreference(pref2);
        mCategory.removePreference(pref3);
        mCategory.removePreference(pref4);
        mCategory.removePreference(pref5);
        mCategory.removePreference(pref6);
        mCategory.removePreference(pref7);
        mCategory.removePreference(pref8);
        mCategory.removePreference(pref9);
        mCategory.removePreference(pref10);
        mCategory.removePreference(pref11);
        
    }

    
 
}
