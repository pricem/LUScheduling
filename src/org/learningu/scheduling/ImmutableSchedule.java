package org.learningu.scheduling;

import static com.google.common.base.Preconditions.checkNotNull;

import org.learningu.scheduling.graph.Program;
import org.learningu.scheduling.graph.Room;
import org.learningu.scheduling.graph.Section;
import org.learningu.scheduling.graph.TimeBlock;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

public final class ImmutableSchedule implements Schedule {
  private final Program program;
  private final ImmutableTable<TimeBlock, Room, Section> scheduleTable;

  public static ImmutableSchedule copyOf(Schedule schedule) {
    checkNotNull(schedule);
    return new ImmutableSchedule(schedule.getProgram(), ImmutableTable.copyOf(schedule
        .getScheduleTable()));
  }

  ImmutableSchedule(Program program, ImmutableTable<TimeBlock, Room, Section> scheduleTable) {
    this.program = checkNotNull(program);
    this.scheduleTable = checkNotNull(scheduleTable);
  }

  @Override
  public Program getProgram() {
    return program;
  }

  @Override
  public Table<TimeBlock, Room, Section> getScheduleTable() {
    return scheduleTable;
  }
}
