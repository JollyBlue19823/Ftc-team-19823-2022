package org.firstinspires.ftc.teamcode.subs;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.path.Path;
import com.acmerobotics.roadrunner.path.PathBuilder;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.acmerobotics.roadrunner.profile.*;


import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.maths.controlLoopMath;
import org.opencv.core.Point;

public class goToPoint {
    final private drive driver;
    Telemetry telemetry;
    private double Kp,Kd,Ki;
    private double maxVel, maxAccel, maxJerk;
    double stateOut,xOut,yOut;
    Gamepad gamepad;
    final private ElapsedTime profileTime = new ElapsedTime();
    private final controlLoopMath headingPID = new controlLoopMath(0,0,0,0);
    controlLoopMath xPID = new controlLoopMath(Kp,Kd,Ki,0);
    controlLoopMath yPID = new controlLoopMath(Kp,Kd,Ki,0);
    private MotionProfile profile = MotionProfileGenerator.generateSimpleMotionProfile(new MotionState(0, 0, 0), new MotionState(1, 0, 0), 2, 3,4);

    public goToPoint(drive driver, Telemetry telemetry, Gamepad gamepad){
        this.driver=driver;
        this.telemetry = telemetry;
        this.gamepad = gamepad;
    }

    //TODO make this work for auto: make the if a while, update the current pose
    public void driveToPoint(Pose2d pose,Pose2d desiredPose,Pose2d startPose,boolean update){
        xPID.setPIDCoeffs(Kp,Kd,Ki,0);
        yPID.setPIDCoeffs(Kp,Kd,Ki,0);
        double distanceNow = Math.abs(Math.hypot(desiredPose.getX()-pose.getX(),desiredPose.getY()-pose.getY()));
        double distanceAtStart = Math.abs(Math.hypot(desiredPose.getX()-startPose.getX(),desiredPose.getY()-startPose.getY()));
        double distance = distanceAtStart-distanceNow;
        double angleToEndPoint = Math.atan2(desiredPose.getY()-startPose.getY(),desiredPose.getX()-startPose.getX());
        if (update) {
            profile = MotionProfileGenerator.generateSimpleMotionProfile(new MotionState(0, 0, 0), new MotionState(distanceAtStart, 0, 0), maxVel, maxAccel, maxJerk);
            profileTime.reset();
        }
        MotionState state = profile.get(profileTime.seconds());
        stateOut = state.getX();
        double slopeToEndPoint = (desiredPose.getY()-startPose.getY())/(desiredPose.getX()-startPose.getX());
        double yInterceptToEndPoint = (desiredPose.getY()-slopeToEndPoint*desiredPose.getX());
        Point statePoint = new Point(startPose.getX()+(stateOut*Math.cos(angleToEndPoint)),(startPose.getX()+(stateOut*Math.cos(angleToEndPoint))*slopeToEndPoint)+yInterceptToEndPoint);
        double distanceToState = Math.abs(Math.hypot(statePoint.x-startPose.getX(),statePoint.y-startPose.getY()));
        xOut = xPID.PIDout(distanceToState*Math.sin(angleToEndPoint)-(pose.getX()-startPose.getX()));
        yOut = yPID.PIDout(distanceToState*Math.cos(angleToEndPoint)-(pose.getY()-startPose.getY()));
        double headingOut = headingPID.PIDout(desiredPose.getHeading()-pose.getHeading());
        driver.driveOut(xOut,-yOut,headingOut,gamepad);

        telemetry.addData("distance: ",distance);
        telemetry.addData("distanceAtStart: ",distanceAtStart);
        telemetry.addData("distanceToProfile",distanceToState);
        telemetry.addData("distanceNow: ",distanceNow);
        telemetry.addData("angleToEndPoint: ", angleToEndPoint);
        telemetry.addData("stateOut: ",stateOut);
        telemetry.addData("xOut: ",xOut);
        telemetry.addData("yOut: ", yOut);
    }

    public void setPIDCoeffs(double Kp, double Kd,double Ki){
        this.Kp = Kp;
        this.Kd = Kd;
        this.Ki = Ki;
    }

    public void setProfileConstraints(double maxVel, double maxAccel, double maxJerk){
        this.maxVel = maxVel;
        this.maxAccel = maxAccel;
        this.maxJerk = maxJerk;
    }
}
