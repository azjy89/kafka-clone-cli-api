package tributary.util;

import tributary.core.tributaryController.TributaryController;
import tributary.core.tributaryController.tributaryClusters.TributaryCluster;

public class TestUtilHelper {
    public static void clearState(TributaryController controller) {
        // Clear topics
        TributaryCluster.getInstance().clear();

        // Clear consumers and producers
        controller.clear();
    }

    public static TributaryController initializeController() {
        TributaryController controller = new TributaryController();
        clearState(controller);
        return controller;
    }

}
