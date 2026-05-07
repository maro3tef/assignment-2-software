package com.masroofy.data;

import com.masroofy.domain.BudgetCycle;

import java.util.List;

/**
 * The interface Budget cycle dao.
 */
public interface IBudgetCycleDAO {
    /**
     * Save cycle boolean.
     *
     * @param cycle the cycle
     * @return the boolean
     */
    boolean saveCycle(BudgetCycle cycle);

    /**
     * Gets current cycle.
     *
     * @return the current cycle
     */
    BudgetCycle getCurrentCycle();

    /**
     * Delete cycle boolean.
     *
     * @param cycleId the cycle id
     * @return the boolean
     */
    boolean deleteCycle(int cycleId);

    /**
     * Gets all cycles.
     *
     * @return the all cycles
     */
    List<BudgetCycle> getAllCycles();

    /**
     * Delete all data boolean.
     *
     * @return the boolean
     */
    boolean deleteAllData();
}