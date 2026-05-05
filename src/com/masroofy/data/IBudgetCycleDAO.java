package com.masroofy.data;

import com.masroofy.domain.BudgetCycle;

import java.util.List;

public interface IBudgetCycleDAO {
    boolean saveCycle(BudgetCycle cycle);
    BudgetCycle getCurrentCycle();
    boolean deleteCycle(int cycleId);
    List<BudgetCycle> getAllCycles();
}