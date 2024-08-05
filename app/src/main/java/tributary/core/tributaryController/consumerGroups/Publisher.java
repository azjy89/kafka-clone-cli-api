package tributary.core.tributaryController.consumerGroups;

import tributary.core.tributaryController.tributaryClusters.Partition;

public interface Publisher {
    public boolean notify(int partitionId);

    public void subscribe(Partition partition);

    public void unsubscribe(Integer partitionId);

    public void unsubscribeAll();
}
