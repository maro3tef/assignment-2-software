package com.masroofy.data;

import com.masroofy.domain.BudgetCycle;

public interface IBudgetCycleDAO {
    boolean saveCycle(BudgetCycle cycle);
    BudgetCycle getCurrentCycle();
    boolean deleteCycle(int cycleId);
}