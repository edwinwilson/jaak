package io.sarl.jaak.demos.ants.behaviors;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import io.sarl.jaak.demos.ants.behaviors.PheromoneFollowingCapacity;
import io.sarl.jaak.demos.ants.environment.Pheromone;
import io.sarl.jaak.environment.external.PhysicBody;
import io.sarl.jaak.environment.external.influence.MotionInfluenceStatus;
import io.sarl.jaak.environment.external.perception.EnvironmentalObject;
import io.sarl.jaak.environment.external.perception.Perceivable;
import io.sarl.jaak.util.RandomNumber;
import io.sarl.lang.annotation.Generated;
import io.sarl.lang.annotation.ImportedCapacityFeature;
import io.sarl.lang.core.Agent;
import io.sarl.lang.core.Behavior;
import java.io.Serializable;
import org.arakhne.afc.math.MathConstants;
import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

/**
 * This abstract class defines a behavior for all ants.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class AbstractAntBehavior extends Behavior {
  /**
   * Select and reply a pheromone.
   * 
   * @param pheromoneType is the type of pheromone to follow
   * @return the pheromone to reach.
   */
  public Pheromone followPheromone(final Class<? extends Pheromone> pheromoneType, final Iterable<? extends Perceivable> perception) {
    boolean _isEmpty = IterableExtensions.isEmpty(perception);
    boolean _not = (!_isEmpty);
    if (_not) {
      PheromoneFollowingCapacity followPheromoneSkill = this.<PheromoneFollowingCapacity>getSkill(PheromoneFollowingCapacity.class);
      Point2f _position = this.getPosition();
      Iterable<Pheromone> _filter = Iterables.<Pheromone>filter(perception, Pheromone.class);
      return followPheromoneSkill.followPheromone(_position, _filter);
    }
    return null;
  }
  
  /**
   * Move randomly.
   */
  public void randomMotion() {
    float _nextFloat = RandomNumber.nextFloat();
    float _nextFloat_1 = RandomNumber.nextFloat();
    float _minus = (_nextFloat - _nextFloat_1);
    float dAngle = (_minus * MathConstants.DEMI_PI);
    if ((dAngle > 0)) {
      this.turnLeft(dAngle);
    } else {
      this.turnRight((-dAngle));
    }
    this.moveForward(1);
  }
  
  /**
   * Turn back.
   */
  public void randomTurnBack() {
    float _nextFloat = RandomNumber.nextFloat();
    float _nextFloat_1 = RandomNumber.nextFloat();
    float _minus = (_nextFloat - _nextFloat_1);
    float dAngle = (_minus * MathConstants.DEMI_PI);
    if ((dAngle > 0)) {
      this.turnLeft((MathConstants.DEMI_PI + dAngle));
    } else {
      this.turnRight((MathConstants.DEMI_PI - dAngle));
    }
    this.moveForward(1);
  }
  
  /**
   * Random patrol.
   */
  public void randomPatrol(final MotionInfluenceStatus status) {
    boolean _equals = Objects.equal(status, MotionInfluenceStatus.NO_MOTION);
    if (_equals) {
      this.randomTurnBack();
    } else {
      this.randomMotion();
    }
  }
  
  /**
   * Go to the given target position.
   * 
   * @param target is the point to reach.
   * @param enableRandom indicates if the random behavior should be used when
   * the given point was already reached.
   * @return <code>true</code> if the ant does not move according to this function,
   * <code>false</code> if the ant is moving.
   */
  public boolean gotoMotion(final Point2f target, final boolean enableRandom) {
    int _x = target.x();
    int _x_1 = this.getX();
    int dx = (_x - _x_1);
    int _y = target.y();
    int _y_1 = this.getY();
    int dy = (_y - _y_1);
    if (((dx != 0) || (dy != 0))) {
      Vector2f motion = new Vector2f(dx, dy);
      motion.normalize();
      this.move(motion, true);
      return false;
    }
    if (enableRandom) {
      this.randomMotion();
      return false;
    }
    return true;
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#dropOff(io.sarl.jaak.environment.external.perception.EnvironmentalObject)}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#dropOff(io.sarl.jaak.environment.external.perception.EnvironmentalObject)
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected void dropOff(final EnvironmentalObject object) {
    getSkill(io.sarl.jaak.environment.external.PhysicBody.class).dropOff(object);
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#getHeadingAngle()}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#getHeadingAngle()
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected float getHeadingAngle() {
    return getSkill(io.sarl.jaak.environment.external.PhysicBody.class).getHeadingAngle();
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#getHeadingVector()}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#getHeadingVector()
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected Vector2f getHeadingVector() {
    return getSkill(io.sarl.jaak.environment.external.PhysicBody.class).getHeadingVector();
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#getPosition()}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#getPosition()
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected Point2f getPosition() {
    return getSkill(io.sarl.jaak.environment.external.PhysicBody.class).getPosition();
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#getSemantic()}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#getSemantic()
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected Serializable getSemantic() {
    return getSkill(io.sarl.jaak.environment.external.PhysicBody.class).getSemantic();
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#getSpeed()}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#getSpeed()
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected Vector2f getSpeed() {
    return getSkill(io.sarl.jaak.environment.external.PhysicBody.class).getSpeed();
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#getX()}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#getX()
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected int getX() {
    return getSkill(io.sarl.jaak.environment.external.PhysicBody.class).getX();
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#getY()}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#getY()
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected int getY() {
    return getSkill(io.sarl.jaak.environment.external.PhysicBody.class).getY();
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#move(org.arakhne.afc.math.continous.object2d.Vector2f)}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#move(org.arakhne.afc.math.continous.object2d.Vector2f)
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected void move(final Vector2f direction) {
    getSkill(io.sarl.jaak.environment.external.PhysicBody.class).move(direction);
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#move(org.arakhne.afc.math.continous.object2d.Vector2f,boolean)}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#move(org.arakhne.afc.math.continous.object2d.Vector2f,boolean)
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected void move(final Vector2f direction, final boolean changeHeading) {
    getSkill(io.sarl.jaak.environment.external.PhysicBody.class).move(direction, changeHeading);
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#moveBackward(int)}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#moveBackward(int)
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected void moveBackward(final int cells) {
    getSkill(io.sarl.jaak.environment.external.PhysicBody.class).moveBackward(cells);
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#moveForward(int)}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#moveForward(int)
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected void moveForward(final int cells) {
    getSkill(io.sarl.jaak.environment.external.PhysicBody.class).moveForward(cells);
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#pickUp(io.sarl.jaak.environment.external.perception.EnvironmentalObject)}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#pickUp(io.sarl.jaak.environment.external.perception.EnvironmentalObject)
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected void pickUp(final EnvironmentalObject object) {
    getSkill(io.sarl.jaak.environment.external.PhysicBody.class).pickUp(object);
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#pickUp(java.lang.Class<? extends io.sarl.jaak.environment.external.perception.Perceivable>)}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#pickUp(java.lang.Class<? extends io.sarl.jaak.environment.external.perception.Perceivable>)
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected Perceivable pickUp(final Class<? extends Perceivable> type) {
    return getSkill(io.sarl.jaak.environment.external.PhysicBody.class).pickUp(type);
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#setHeading(float)}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#setHeading(float)
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected void setHeading(final float radians) {
    getSkill(io.sarl.jaak.environment.external.PhysicBody.class).setHeading(radians);
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#setHeading(org.arakhne.afc.math.continous.object2d.Vector2f)}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#setHeading(org.arakhne.afc.math.continous.object2d.Vector2f)
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected void setHeading(final Vector2f direction) {
    getSkill(io.sarl.jaak.environment.external.PhysicBody.class).setHeading(direction);
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#setSemantic(java.io.Serializable)}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#setSemantic(java.io.Serializable)
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected void setSemantic(final Serializable semantic) {
    getSkill(io.sarl.jaak.environment.external.PhysicBody.class).setSemantic(semantic);
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#synchronizeBody()}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#synchronizeBody()
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected void synchronizeBody() {
    getSkill(io.sarl.jaak.environment.external.PhysicBody.class).synchronizeBody();
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#touchUp(java.lang.Class<? extends io.sarl.jaak.environment.external.perception.EnvironmentalObject>)}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#touchUp(java.lang.Class<? extends io.sarl.jaak.environment.external.perception.EnvironmentalObject>)
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected EnvironmentalObject touchUp(final Class<? extends EnvironmentalObject> type) {
    return getSkill(io.sarl.jaak.environment.external.PhysicBody.class).touchUp(type);
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#turnLeft(float)}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#turnLeft(float)
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected void turnLeft(final float radians) {
    getSkill(io.sarl.jaak.environment.external.PhysicBody.class).turnLeft(radians);
  }
  
  /**
   * See the capacity {@link io.sarl.jaak.environment.external.PhysicBody#turnRight(float)}.
   * 
   * @see io.sarl.jaak.environment.external.PhysicBody#turnRight(float)
   */
  @Generated
  @ImportedCapacityFeature(PhysicBody.class)
  protected void turnRight(final float radians) {
    getSkill(io.sarl.jaak.environment.external.PhysicBody.class).turnRight(radians);
  }
  
  /**
   * Construct a behavior.
   * @param owner - reference to the agent that is owning this behavior.
   */
  @Generated
  public AbstractAntBehavior(final Agent owner) {
    super(owner);
  }
}
