package org.learningu.scheduling.logic;

import org.learningu.scheduling.PresentAssignment;
import org.learningu.scheduling.Schedule;
import org.learningu.scheduling.StartAssignment;
import org.learningu.scheduling.graph.Course;
import org.learningu.scheduling.graph.Program;
import org.learningu.scheduling.graph.Room;
import org.learningu.scheduling.util.Flag;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public final class LocalConflictLogic extends ScheduleLogic {
  @Flag(
      value = "minClassCapRatio",
      defaultValue = "1.0",
      description = "The minimum ratio of the maximum capacity of a class to the capacity of the room for which it is scheduled. "
          + "For example, if this was 1.25, the class would be forced to use a room at least 25% bigger than its cap. "
          + "The default value is 1.0.")
  private final double minClassCapRatio;

  @Flag(
      value = "maxEstClassSizeRatio",
      defaultValue = "Infinity",
      description = "The maximum ratio of the estimated size of a class to the capacity of the room for which it is scheduled. "
          + "For example, if this was 3.0, the class would be forced to use a room at most 3 times bigger than its expected size. "
          + "The default value is infinity.")
  private final double maxEstClassSizeRatio;

  @Inject
  LocalConflictLogic(@Named("minClassCapRatio") double minClassCapRatio,
      @Named("maxEstClassSizeRatio") double maxEstClassSizeRatio) {
    this.minClassCapRatio = minClassCapRatio;
    this.maxEstClassSizeRatio = maxEstClassSizeRatio;
    classCapRatioConditionText = "Class cap : room capacity ratio must be >= " + minClassCapRatio;
    estSizeRatioConditionText = "Est class size : room capacity ratio must be <= "
        + maxEstClassSizeRatio;
  }

  transient final String classCapRatioConditionText;

  transient final String estSizeRatioConditionText;

  @Override
  public void validate(ScheduleValidator validator, Schedule schedule, StartAssignment assignment) {
    super.validate(validator, schedule, assignment);
    Course course = assignment.getCourse();
    Room room = assignment.getRoom();
    double estClassSizeRatio = ((double) course.getEstimatedClassSize()) / room.getCapacity();
    validator.validateLocal(
        estClassSizeRatio <= maxEstClassSizeRatio,
        assignment,
        estSizeRatioConditionText);
    double classCapRatio = ((double) course.getMaxClassSize()) / room.getCapacity();
    validator.validateLocal(
        classCapRatio >= minClassCapRatio,
        assignment,
        classCapRatioConditionText);
  }

  @Override
  public void validate(ScheduleValidator validator, Schedule schedule, PresentAssignment assignment) {
    super.validate(validator, schedule, assignment);
    Program program = schedule.getProgram();
    validator.validateLocal(
        program.compatiblePeriods(assignment.getCourse()).contains(assignment.getPeriod()),
        assignment,
        "All teachers for a course must be available during all periods in which it is scheduled");
    validator.validateLocal(
        program.compatiblePeriods(assignment.getRoom()).contains(assignment.getPeriod()),
        assignment,
        "Courses cannot be scheduled to rooms while the room is unavailable");
  }
}