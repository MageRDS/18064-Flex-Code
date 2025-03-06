package org.firstinspires.ftc.teamcode;
import androidx.annotation.NonNull;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.teamcode.MecanumDrive;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import android.app.Activity;
import com.qualcomm.robotcore.hardware.Servo;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import java.util.Locale;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.concurrent.TimeUnit;

@Config
@Autonomous(name = "RED_BUCKET_SIDE", group = "Autonomous")
public class REDBUCKET extends LinearOpMode {
    public class Lift {
        private DcMotorEx ylinear;
        private DcMotorEx ylinear2;
        public Lift(HardwareMap hardwareMap) {
            ylinear = hardwareMap.get(DcMotorEx.class, "ylinear");
            ylinear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            ylinear.setDirection(DcMotorSimple.Direction.FORWARD);
            ylinear2 = hardwareMap.get(DcMotorEx.class, "ylinear2");
            ylinear2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            ylinear2.setDirection(DcMotorSimple.Direction.FORWARD);
        }

        public class LiftUp implements Action {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    ylinear.setPower(-1);
                    ylinear2.setPower(1);
                    initialized = true;
                }

                double pos = ylinear2.getCurrentPosition();
                packet.put("liftPos", pos);
                if (pos < 4150.0) {
                    return true;
                } else {
                    ylinear.setPower(0);
                    ylinear2.setPower(0);
                    return false;
                }
            }
        }
        public Action liftUp() {
            return new LiftUp();
        }

        public class LiftDown implements Action {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    ylinear.setPower(0.8);
                    ylinear2.setPower(-0.8);
                    initialized = true;
                }

                double pos = ylinear2.getCurrentPosition();
                packet.put("liftPos", pos);
                if (pos > 220.0) {
                    telemetry.addData("Liftdown", ylinear2.getCurrentPosition());
                    return true;
                } else {
                    ylinear.setPower(0);
                    ylinear.setPower(0);
                    return false;
                }
            }
        }
        public Action liftDown(){
            return new LiftDown();
        }
    }
    public class Intake {
        private Servo xlinear;
        private CRServo intake;
        private CRServo intake2;
        public Intake(HardwareMap hardwareMap) {
            xlinear = hardwareMap.get(Servo.class, "xlinear");
            intake = hardwareMap.get(CRServo.class, "intake");
            intake2 = hardwareMap.get(CRServo.class, "intake2");
        }

        public class IntakeIn implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {

                intake.setPower(-1);
                intake2.setPower(1);
                xlinear.setPosition(0.25);
                //sleep(1000);
                return false;
            }
        }
        public Action IntakeIn() {
            return new IntakeIn();
        }
        public class IntakeUp implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {

                intake.setPower(0);
                intake2.setPower(0);
                xlinear.setPosition(0.8);
                //sleep(1000);
                return false;
            }
        }
        public Action IntakeUp() {
            return new IntakeUp();
        }
        public class IntakeStop implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {

                intake.setPower(0);
                intake2.setPower(0);
                xlinear.setPosition(0.8);
                //sleep(1000);
                return false;
            }
        }

        public Action IntakeStop() {
            return new IntakeStop();
        }

        public class FlipDown implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                xlinear.setPosition(0.25);
                intake.setPower(0.8);
                intake2.setPower(-0.8);
                sleep(700);
                return false;
            }
        }
        public Action FlipDown() {
            return new FlipDown();
        }

        public class Outake implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                xlinear.setPosition(0.7);
                intake.setPower(-0.8);
                intake2.setPower(0.8);
                sleep(500);
                xlinear.setPosition(0.8);
                sleep(500);
                //xlinear.setPosition(0.8);

                intake.setPower(0);
                intake2.setPower(0);
                return false;
            }
        }

        public Action Outake() {
            return new Outake();
        }
    }
    /*public class Claw {
        private Servo claw;

        public Claw(HardwareMap hardwareMap) {
            claw = hardwareMap.get(Servo.class, "claw");
        }

        public class CloseClaw implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                claw.setPosition(0.5);
                return false;
            }
        }
        public Action closeClaw() {
            return new CloseClaw();
        }

        public class OpenClaw implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                claw.setPosition(1.0);
                return false;
            }
        }
        public Action openClaw() {
            return new OpenClaw();
        }
    }*/

    @Override
    public void runOpMode() {
        Pose2d initialPose = new Pose2d(0, 0, Math.toRadians(90));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);
        //Claw claw = new Claw(hardwareMap);
        Lift lift = new Lift(hardwareMap);
        Intake intake = new Intake(hardwareMap);
        // vision here that outputs position
        int visionOutputPosition = 1;


        TrajectoryActionBuilder tab1 = drive.actionBuilder(initialPose)
                .strafeToConstantHeading(new Vector2d(0, 0))
                .splineToSplineHeading(new Pose2d(-10, 14, Math.toRadians(230)), Math.toRadians(190));
                //.waitSeconds(0.5);
        TrajectoryActionBuilder tab2 = drive.actionBuilder(new Pose2d(-10,14 ,Math.toRadians(230)))
                .setReversed(true)
                .splineToSplineHeading(new Pose2d(3, 27, Math.toRadians(90)), Math.toRadians(0));
        //.strafeTo(new Vector2d(-24, 7))
                //.turn(Math.toRadians(-120))
                //.strafeTo(new Vector2d(-24, 22));
        //.splineToSplineHeading(new Pose2d(-7, 18, Math.toRadians(90)), Math.toRadians(0));
        //.strafeTo(new Vector2d(19, 38));
        TrajectoryActionBuilder tabenter = drive.actionBuilder( new Pose2d(3,27,Math.toRadians(90)))
                .splineToSplineHeading(new Pose2d(-9, 17, Math.toRadians(230)), Math.toRadians(180));

        //.strafeTo(new Vector2d(14.5,38));
        //.strafeTo(new Vector2d(16,38));
        TrajectoryActionBuilder tab3 = drive.actionBuilder( new Pose2d(-24,22,Math.toRadians(90)))
                .splineToSplineHeading(new Pose2d(-19, 20, Math.toRadians(90)), Math.toRadians(0));





        TrajectoryActionBuilder tab4 = drive.actionBuilder(new Pose2d(-22,10,Math.toRadians(250)))
                .turn(Math.toRadians(-152))
                //.splineToSplineHeading(new Pose2d(12, 36, Math.toRadians(100)), Math.toRadians(180))
                //.turn(Math.toRadians(95))
                .strafeTo(new Vector2d(-23,17));
        TrajectoryActionBuilder tabexit = drive.actionBuilder( new Pose2d(-23,17,Math.toRadians(108)))
                .turn(Math.toRadians(-40))
                .strafeTo(new Vector2d(-23, 21))
                .turn(Math.toRadians(40))
                .waitSeconds(1);
        //.strafeTo(new Vector2d(16,38));
        TrajectoryActionBuilder tab5 = drive.actionBuilder(new Pose2d(-23,21,Math.toRadians(108)))
                .turn(Math.toRadians(-213))
                .strafeTo(new Vector2d(-19,6))
                .waitSeconds(0.5);

        Action trajectoryActionCloseOut = tab1.endTrajectory().fresh()
                //.strafeTo(new Vector2d(-25, 8))
                .waitSeconds(0.5)
                .build();
        Action trajectoryActionCloseOut2 = tab2.endTrajectory().fresh()
                //.strafeTo(new Vector2d(17, 38))
                .waitSeconds(0.5)
                .build();
        Action trajectoryActionCloseOut3 = tab3.endTrajectory().fresh()
                //.strafeTo(new Vector2d(5, 13))
                .waitSeconds(0.5)
                .build();
        Action trajectoryActionCloseOut4 = tab4.endTrajectory().fresh()
                //.strafeTo(new Vector2d(9, 36))
                .waitSeconds(0.5)
                .build();
        Action trajectoryActionCloseOut5 = tab5.endTrajectory().fresh()
                //.strafeTo(new Vector2d(5, 13))
                .waitSeconds(0.5)
                .build();
        // actions that need to happen on init; for instance, a claw tightening.
        //Actions.runBlocking(claw.closeClaw());


        while (!isStopRequested() && !opModeIsActive()) {
            int position = visionOutputPosition;
            telemetry.addData("Position during Init", position);
            telemetry.update();
        }

        int startPosition = visionOutputPosition;
        telemetry.addData("Starting Position", startPosition);
        telemetry.update();
        waitForStart();

        if (isStopRequested()) return;

        Action trajectoryActionChosen;
        trajectoryActionChosen = tab1.build();

        Action trajectoryActionChosen2;
        trajectoryActionChosen2 = tab2.build();

        Action trajectoryActionChosen3;
        trajectoryActionChosen3 = tab3.build();

        Action trajectoryActionChosen4;
        trajectoryActionChosen4 = tab4.build();

        Action trajectoryActionChosen5;
        trajectoryActionChosen5 = tab5.build();
        Action trajectoryActionChosenenter;
        trajectoryActionChosenenter = tabenter.build();
        Action trajectoryActionChosenexit;
        trajectoryActionChosenexit = tabexit.build();


        Actions.runBlocking(

                new SequentialAction(
                        intake.IntakeUp(),
                        new ParallelAction(
                                trajectoryActionChosen,
                                lift.liftUp()
                        ),

                        intake.Outake(),


                        new ParallelAction(
                                trajectoryActionChosen2,
                                lift.liftDown()
                        ),
                        intake.FlipDown(),

                        new ParallelAction(
                                trajectoryActionChosenenter,
                                intake.IntakeUp(),
                                lift.liftUp()
                        ),
                        intake.Outake(),




                        new ParallelAction(
                                lift.liftDown(),
                                trajectoryActionChosen3
                        )/*
                        intake.Outake(),
                        new ParallelAction(
                                trajectoryActionChosen4,
                                lift.liftDown()
                        ),
                        intake.FlipDown(),
                        new ParallelAction(
                                trajectoryActionChosenexit,
                                intake.IntakeIn()
                        ),
                        intake.IntakeStop(),
                        new ParallelAction(
                                trajectoryActionChosen5,
                                lift.liftUp()
                        ),
                        intake.Outake()
                        */)
        );
    }
}