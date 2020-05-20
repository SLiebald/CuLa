package com.sliebald.cula.utilities;

import com.sliebald.cula.MyApplication;
import com.sliebald.cula.data.CulaRepository;
import com.sliebald.cula.data.database.CulaDatabase;

/**
 * Provides Utility methods for injecting e.g. the {@link CulaRepository} in other classes (e.g.
 * the ViewModels).
 */
public class InjectorUtils {

    /**
     * Provides the {@link CulaRepository} for other classes for data access. Uses the
     * Application context provided by {@link MyApplication}.
     *
     * @return The {@link CulaRepository}
     */
    //TODO: refactor with proper injection
    public static CulaRepository provideRepository() {
        CulaDatabase database = CulaDatabase.getInstance(MyApplication.Companion.getContext());
        AppExecutors executors = AppExecutors.getInstance();
        return CulaRepository.getInstance(database, executors);
    }
}