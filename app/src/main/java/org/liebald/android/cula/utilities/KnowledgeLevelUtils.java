package org.liebald.android.cula.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.res.ResourcesCompat;

import org.liebald.android.cula.R;

public class KnowledgeLevelUtils {
    /**
     * The absolute minimum of the KnowledgeLevel.
     */
    private static final int LEVEL_MIN = 0;

    /**
     * The absolute maximum of the KnowledgeLevel.
     */
    private static final int LEVEL_MAX = 5;


    /**
     * Resolves the given knowledgeLevel to color.
     *
     * @param context        Context required to access the resources.
     * @param knowledgeLevel The knowledgeLevel.
     * @return The color of the given KnowledgeLevel.
     */
    public static int getColorByKnowledgeLevel(Context context, double knowledgeLevel) {
        Resources res = context.getResources();
        if (knowledgeLevel < 1)
            return ResourcesCompat.getColor(res, R.color.knowledgeLevel_1, null);
        else if (knowledgeLevel < 2)
            return ResourcesCompat.getColor(res, R.color.knowledgeLevel_2, null);
        else if (knowledgeLevel < 3)
            return ResourcesCompat.getColor(res, R.color.knowledgeLevel_3, null);
        else if (knowledgeLevel < 4)
            return ResourcesCompat.getColor(res, R.color.knowledgeLevel_4, null);
        else
            return ResourcesCompat.getColor(res, R.color.knowledgeLevel_5, null);
    }

    /**
     * Resolves the given knowledgeLevel to the fitting name.
     *
     * @param context        Context required to access the resources.
     * @param knowledgeLevel The knowledgeLevel.
     * @return The name of the given KnowledgeLevel
     */
    public static String getNameByKnowledgeLevel(Context context, double knowledgeLevel) {
        Resources res = context.getResources();
        if (knowledgeLevel < 1)
            return res.getString(R.string.knowledgeLevel1);
        else if (knowledgeLevel < 2)
            return res.getString(R.string.knowledgeLevel2);
        else if (knowledgeLevel < 3)
            return res.getString(R.string.knowledgeLevel3);
        else if (knowledgeLevel < 4)
            return res.getString(R.string.knowledgeLevel4);
        else
            return res.getString(R.string.knowledgeLevel5);
    }


    /**
     * Calculates a new KnowledgeLevel depending on whether it should be increased or decreased.
     *
     * @param oldLevel      The last level which serves as ase
     * @param increaseLevel if true the level is incremented, if false decremented.
     * @return The updated KnowledgeLevel
     */
    public static double calculateKnowlevelAdjustment(double oldLevel, boolean increaseLevel) {
        double newLevel;
        // Increments/Decrements could be changed to a more dynamic approach (e.g. more or less
        // or depending on how often a word was trained.
        if (increaseLevel) {
            newLevel = oldLevel + 0.5;
            if (newLevel > LEVEL_MAX)
                newLevel = LEVEL_MAX;
        } else {
            newLevel = oldLevel - 0.5;
            if (newLevel < LEVEL_MIN)
                newLevel = LEVEL_MIN;
        }
        return newLevel;
    }

}
