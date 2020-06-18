import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import cn.zju.id21932036.yutianyi.R;

/**
 * Created by 82307 on 2020/5/24.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
