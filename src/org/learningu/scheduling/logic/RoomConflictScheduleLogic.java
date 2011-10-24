package org.learningu.scheduling.logic;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.learningu.scheduling.Schedule;
import org.learningu.scheduling.StartAssignment;

import com.google.common.base.Optional;
import com.google.common.collect.DiscreteDomains;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;

final class RoomConflictScheduleLogic extends ScheduleLogic {

  @Override
  public void validate(ScheduleValidator validator, Schedule schedule, StartAssignment assignment) {
    super.validate(validator, schedule, assignment);
    // The first thing to start before the *last* period of assignment should not overlap assignment. 
    Optional<StartAssignment> startingBefore = schedule.startingBefore(
        assignment.getRoom(),
        getLast(assignment.getPresentPeriods()));
    validator.validateGlobal(
        !(startingBefore.isPresent() && overlaps(assignment, startingBefore.get())),
        assignment,
        startingBefore.asSet(),
        "classes may not use the same room at the same time");
  }

  private boolean overlaps(StartAssignment assign1, StartAssignment assign2) {
    Range<Integer> range1 = periodIndexRange(assign1);
    Range<Integer> range2 = periodIndexRange(assign2);
    return !assign1.getTimeBlock().equals(assign2.getTimeBlock()) || !range1.isConnected(range2)
        || range1.intersection(range2).canonical(DiscreteDomains.integers()).isEmpty();
  }

  private static Range<Integer> periodIndexRange(StartAssignment assign) {
    int start = assign.getPeriod().getIndex();
    int length = assign.getCourse().getPeriodLength();
    return Ranges.closedOpen(start, start + length);
  }

  private static <E> E getLast(List<E> list) {
    checkArgument(!list.isEmpty());
    return list.get(list.size() - 1);
  }
}