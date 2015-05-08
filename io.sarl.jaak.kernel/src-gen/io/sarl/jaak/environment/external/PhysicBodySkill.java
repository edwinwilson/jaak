package io.sarl.jaak.environment.external;

import com.google.common.base.Objects;
import io.sarl.core.AgentSpawned;
import io.sarl.core.Behaviors;
import io.sarl.core.DefaultContextInteractions;
import io.sarl.jaak.environment.external.PhysicBody;
import io.sarl.jaak.environment.external.body.TurtleObject;
import io.sarl.jaak.environment.external.influence.DropDownInfluence;
import io.sarl.jaak.environment.external.influence.MotionInfluence;
import io.sarl.jaak.environment.external.influence.PickUpInfluence;
import io.sarl.jaak.environment.external.influence.SemanticChangeInfluence;
import io.sarl.jaak.environment.external.perception.EnvironmentalObject;
import io.sarl.jaak.environment.external.perception.Perceivable;
import io.sarl.jaak.kernel.external.JaakPhysicSpace;
import io.sarl.jaak.kernel.external.JaakPhysicSpaceConstants;
import io.sarl.jaak.kernel.internal.JaakPhysicSpaceSpecification;
import io.sarl.jaak.kernel.internal.SkillBinder;
import io.sarl.lang.annotation.DefaultValue;
import io.sarl.lang.annotation.DefaultValueSource;
import io.sarl.lang.annotation.DefaultValueUse;
import io.sarl.lang.annotation.FiredEvent;
import io.sarl.lang.annotation.Generated;
import io.sarl.lang.annotation.ImportedCapacityFeature;
import io.sarl.lang.core.Address;
import io.sarl.lang.core.Agent;
import io.sarl.lang.core.AgentContext;
import io.sarl.lang.core.Behavior;
import io.sarl.lang.core.Event;
import io.sarl.lang.core.EventListener;
import io.sarl.lang.core.EventSpace;
import io.sarl.lang.core.Scope;
import io.sarl.lang.core.Skill;
import io.sarl.lang.core.Space;
import io.sarl.lang.core.SpaceID;
import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;
import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Vector2f;

@SuppressWarnings("all")
public class PhysicBodySkill extends Skill implements PhysicBody {
  protected JaakPhysicSpace physicSpace;
  
  protected SkillBinder binder;
  
  public void install() {
    AgentContext dc = this.getDefaultContext();
    UUID spaceId = JaakPhysicSpaceConstants.getSpaceIDInContext(dc);
    JaakPhysicSpace _orCreateSpaceWithSpec = dc.<JaakPhysicSpace>getOrCreateSpaceWithSpec(JaakPhysicSpaceSpecification.class, spaceId);
    this.physicSpace = _orCreateSpaceWithSpec;
    if ((this.physicSpace == null)) {
      throw new IllegalStateException("No physic space found");
    }
    EventListener _asEventListener = this.asEventListener();
    SkillBinder _skillBinder = new SkillBinder(_asEventListener);
    this.binder = _skillBinder;
    this.physicSpace.spawnBody(this.binder);
  }
  
  public void uninstall() {
    if (this.physicSpace!=null) {
      this.physicSpace.killBody(this.binder);
    }
    this.physicSpace = null;
    this.binder = null;
  }
  
  public void synchronizeBody() {
    float _currentTime = this.binder.getCurrentTime();
    this.physicSpace.influence(_currentTime, null);
  }
  
  @DefaultValueSource
  public void move(final Vector2f direction, @DefaultValue("io.sarl.jaak.environment.external.PhysicBody#MOVE_1") final boolean changeHeading) {
    float _currentTime = this.binder.getCurrentTime();
    TurtleObject _body = this.binder.getBody();
    MotionInfluence _motionInfluence = new MotionInfluence(_body, direction);
    this.physicSpace.influence(_currentTime, _motionInfluence);
    if (changeHeading) {
      this.setHeading(direction);
    }
  }
  
  public void moveForward(final int cells) {
    TurtleObject body = this.binder.getBody();
    Vector2f _headingVector = body.getHeadingVector();
    Vector2f view = _headingVector.clone();
    view.normalize();
    view.scale(cells);
    float _currentTime = this.binder.getCurrentTime();
    MotionInfluence _motionInfluence = new MotionInfluence(body, view);
    this.physicSpace.influence(_currentTime, _motionInfluence);
  }
  
  public void moveBackward(final int cells) {
    TurtleObject body = this.binder.getBody();
    Vector2f _headingVector = body.getHeadingVector();
    Vector2f view = _headingVector.clone();
    view.normalize();
    view.scale((-cells));
    float _currentTime = this.binder.getCurrentTime();
    MotionInfluence _motionInfluence = new MotionInfluence(body, view);
    this.physicSpace.influence(_currentTime, _motionInfluence);
  }
  
  public void turnLeft(final float radians) {
    float _currentTime = this.binder.getCurrentTime();
    TurtleObject _body = this.binder.getBody();
    MotionInfluence _motionInfluence = new MotionInfluence(_body, (-radians));
    this.physicSpace.influence(_currentTime, _motionInfluence);
  }
  
  public void turnRight(final float radians) {
    float _currentTime = this.binder.getCurrentTime();
    TurtleObject _body = this.binder.getBody();
    MotionInfluence _motionInfluence = new MotionInfluence(_body, radians);
    this.physicSpace.influence(_currentTime, _motionInfluence);
  }
  
  public void setHeading(final float radians) {
    TurtleObject body = this.binder.getBody();
    float _headingAngle = body.getHeadingAngle();
    float v = (radians - _headingAngle);
    float _currentTime = this.binder.getCurrentTime();
    MotionInfluence _motionInfluence = new MotionInfluence(body, v);
    this.physicSpace.influence(_currentTime, _motionInfluence);
  }
  
  public void setHeading(final Vector2f direction) {
    float _orientationAngle = direction.getOrientationAngle();
    this.setHeading(_orientationAngle);
  }
  
  public void dropOff(final EnvironmentalObject object) {
    float _currentTime = this.binder.getCurrentTime();
    TurtleObject _body = this.binder.getBody();
    DropDownInfluence _dropDownInfluence = new DropDownInfluence(_body, object);
    this.physicSpace.influence(_currentTime, _dropDownInfluence);
  }
  
  public Perceivable pickUp(final Class<? extends Perceivable> type) {
    TurtleObject body = this.binder.getBody();
    Collection<EnvironmentalObject> _perceivedObjects = body.getPerceivedObjects();
    for (final EnvironmentalObject obj : _perceivedObjects) {
      boolean _and = false;
      boolean _isInstance = type.isInstance(obj);
      if (!_isInstance) {
        _and = false;
      } else {
        Point2f _position = body.getPosition();
        Point2f _position_1 = obj.getPosition();
        boolean _equals = Objects.equal(_position, _position_1);
        _and = _equals;
      }
      if (_and) {
        float _currentTime = this.binder.getCurrentTime();
        PickUpInfluence _pickUpInfluence = new PickUpInfluence(body, obj);
        this.physicSpace.influence(_currentTime, _pickUpInfluence);
        return type.cast(obj);
      }
    }
    return null;
  }
  
  public void pickUp(final EnvironmentalObject object) {
    float _currentTime = this.binder.getCurrentTime();
    TurtleObject _body = this.binder.getBody();
    PickUpInfluence _pickUpInfluence = new PickUpInfluence(_body, object);
    this.physicSpace.influence(_currentTime, _pickUpInfluence);
  }
  
  public EnvironmentalObject touchUp(final Class<? extends EnvironmentalObject> type) {
    TurtleObject _body = this.binder.getBody();
    Collection<EnvironmentalObject> _perceivedObjects = _body.getPerceivedObjects();
    for (final EnvironmentalObject obj : _perceivedObjects) {
      boolean _and = false;
      boolean _isInstance = type.isInstance(obj);
      if (!_isInstance) {
        _and = false;
      } else {
        Point2f _position = this.getPosition();
        Point2f _position_1 = obj.getPosition();
        boolean _equals = Objects.equal(_position, _position_1);
        _and = _equals;
      }
      if (_and) {
        return type.cast(obj);
      }
    }
    return null;
  }
  
  public void setSemantic(final Serializable semantic) {
    float _currentTime = this.binder.getCurrentTime();
    SemanticChangeInfluence _semanticChangeInfluence = new SemanticChangeInfluence(semantic);
    this.physicSpace.influence(_currentTime, _semanticChangeInfluence);
  }
  
  public Point2f getPosition() {
    TurtleObject _body = this.binder.getBody();
    return _body.getPosition();
  }
  
  public float getHeadingAngle() {
    TurtleObject _body = this.binder.getBody();
    return _body.getHeadingAngle();
  }
  
  public Vector2f getHeadingVector() {
    TurtleObject _body = this.binder.getBody();
    return _body.getHeadingVector();
  }
  
  public Serializable getSemantic() {
    TurtleObject _body = this.binder.getBody();
    return _body.getSemantic();
  }
  
  public float getSpeed() {
    TurtleObject _body = this.binder.getBody();
    return _body.getSpeed();
  }
  
  public int getX() {
    Point2f _position = this.getPosition();
    return _position.x();
  }
  
  public int getY() {
    Point2f _position = this.getPosition();
    return _position.y();
  }
  
  /**
   * See the capacity {@link io.sarl.core.DefaultContextInteractions#emit(io.sarl.lang.core.Event)}.
   * 
   * @see io.sarl.core.DefaultContextInteractions#emit(io.sarl.lang.core.Event)
   */
  @Generated
  @ImportedCapacityFeature(DefaultContextInteractions.class)
  protected void emit(final Event e) {
    getSkill(io.sarl.core.DefaultContextInteractions.class).emit(e);
  }
  
  /**
   * See the capacity {@link io.sarl.core.DefaultContextInteractions#emit(io.sarl.lang.core.Event,io.sarl.lang.core.Scope<io.sarl.lang.core.Address>)}.
   * 
   * @see io.sarl.core.DefaultContextInteractions#emit(io.sarl.lang.core.Event,io.sarl.lang.core.Scope<io.sarl.lang.core.Address>)
   */
  @Generated
  @ImportedCapacityFeature(DefaultContextInteractions.class)
  protected void emit(final Event e, final Scope<Address> scope) {
    getSkill(io.sarl.core.DefaultContextInteractions.class).emit(e, scope);
  }
  
  /**
   * See the capacity {@link io.sarl.core.DefaultContextInteractions#getDefaultAddress()}.
   * 
   * @see io.sarl.core.DefaultContextInteractions#getDefaultAddress()
   */
  @Generated
  @ImportedCapacityFeature(DefaultContextInteractions.class)
  protected Address getDefaultAddress() {
    return getSkill(io.sarl.core.DefaultContextInteractions.class).getDefaultAddress();
  }
  
  /**
   * See the capacity {@link io.sarl.core.DefaultContextInteractions#getDefaultContext()}.
   * 
   * @see io.sarl.core.DefaultContextInteractions#getDefaultContext()
   */
  @Generated
  @ImportedCapacityFeature(DefaultContextInteractions.class)
  protected AgentContext getDefaultContext() {
    return getSkill(io.sarl.core.DefaultContextInteractions.class).getDefaultContext();
  }
  
  /**
   * See the capacity {@link io.sarl.core.DefaultContextInteractions#getDefaultSpace()}.
   * 
   * @see io.sarl.core.DefaultContextInteractions#getDefaultSpace()
   */
  @Generated
  @ImportedCapacityFeature(DefaultContextInteractions.class)
  protected EventSpace getDefaultSpace() {
    return getSkill(io.sarl.core.DefaultContextInteractions.class).getDefaultSpace();
  }
  
  /**
   * See the capacity {@link io.sarl.core.DefaultContextInteractions#isDefaultContext(io.sarl.lang.core.AgentContext)}.
   * 
   * @see io.sarl.core.DefaultContextInteractions#isDefaultContext(io.sarl.lang.core.AgentContext)
   */
  @Generated
  @ImportedCapacityFeature(DefaultContextInteractions.class)
  protected boolean isDefaultContext(final AgentContext context) {
    return getSkill(io.sarl.core.DefaultContextInteractions.class).isDefaultContext(context);
  }
  
  /**
   * See the capacity {@link io.sarl.core.DefaultContextInteractions#isDefaultContext(java.util.UUID)}.
   * 
   * @see io.sarl.core.DefaultContextInteractions#isDefaultContext(java.util.UUID)
   */
  @Generated
  @ImportedCapacityFeature(DefaultContextInteractions.class)
  protected boolean isDefaultContext(final UUID contextID) {
    return getSkill(io.sarl.core.DefaultContextInteractions.class).isDefaultContext(contextID);
  }
  
  /**
   * See the capacity {@link io.sarl.core.DefaultContextInteractions#isDefaultSpace(io.sarl.lang.core.Space)}.
   * 
   * @see io.sarl.core.DefaultContextInteractions#isDefaultSpace(io.sarl.lang.core.Space)
   */
  @Generated
  @ImportedCapacityFeature(DefaultContextInteractions.class)
  protected boolean isDefaultSpace(final Space space) {
    return getSkill(io.sarl.core.DefaultContextInteractions.class).isDefaultSpace(space);
  }
  
  /**
   * See the capacity {@link io.sarl.core.DefaultContextInteractions#isDefaultSpace(io.sarl.lang.core.SpaceID)}.
   * 
   * @see io.sarl.core.DefaultContextInteractions#isDefaultSpace(io.sarl.lang.core.SpaceID)
   */
  @Generated
  @ImportedCapacityFeature(DefaultContextInteractions.class)
  protected boolean isDefaultSpace(final SpaceID space) {
    return getSkill(io.sarl.core.DefaultContextInteractions.class).isDefaultSpace(space);
  }
  
  /**
   * See the capacity {@link io.sarl.core.DefaultContextInteractions#isDefaultSpace(java.util.UUID)}.
   * 
   * @see io.sarl.core.DefaultContextInteractions#isDefaultSpace(java.util.UUID)
   */
  @Generated
  @ImportedCapacityFeature(DefaultContextInteractions.class)
  protected boolean isDefaultSpace(final UUID space) {
    return getSkill(io.sarl.core.DefaultContextInteractions.class).isDefaultSpace(space);
  }
  
  /**
   * See the capacity {@link io.sarl.core.DefaultContextInteractions#isInDefaultSpace(io.sarl.lang.core.Event)}.
   * 
   * @see io.sarl.core.DefaultContextInteractions#isInDefaultSpace(io.sarl.lang.core.Event)
   */
  @Generated
  @ImportedCapacityFeature(DefaultContextInteractions.class)
  protected boolean isInDefaultSpace(final Event event) {
    return getSkill(io.sarl.core.DefaultContextInteractions.class).isInDefaultSpace(event);
  }
  
  /**
   * See the capacity {@link io.sarl.core.DefaultContextInteractions#receive(java.util.UUID,io.sarl.lang.core.Event)}.
   * 
   * @see io.sarl.core.DefaultContextInteractions#receive(java.util.UUID,io.sarl.lang.core.Event)
   */
  @Generated
  @ImportedCapacityFeature(DefaultContextInteractions.class)
  protected void receive(final UUID receiver, final Event e) {
    getSkill(io.sarl.core.DefaultContextInteractions.class).receive(receiver, e);
  }
  
  /**
   * See the capacity {@link io.sarl.core.DefaultContextInteractions#spawn(java.lang.Class<? extends io.sarl.lang.core.Agent>,java.lang.Object[])}.
   * 
   * @see io.sarl.core.DefaultContextInteractions#spawn(java.lang.Class<? extends io.sarl.lang.core.Agent>,java.lang.Object[])
   */
  @FiredEvent(AgentSpawned.class)
  @Generated
  @ImportedCapacityFeature(DefaultContextInteractions.class)
  protected UUID spawn(final Class<? extends Agent> aAgent, final Object... params) {
    return getSkill(io.sarl.core.DefaultContextInteractions.class).spawn(aAgent, params);
  }
  
  /**
   * See the capacity {@link io.sarl.core.Behaviors#asEventListener()}.
   * 
   * @see io.sarl.core.Behaviors#asEventListener()
   */
  @Generated
  @ImportedCapacityFeature(Behaviors.class)
  protected EventListener asEventListener() {
    return getSkill(io.sarl.core.Behaviors.class).asEventListener();
  }
  
  /**
   * See the capacity {@link io.sarl.core.Behaviors#registerBehavior(io.sarl.lang.core.Behavior)}.
   * 
   * @see io.sarl.core.Behaviors#registerBehavior(io.sarl.lang.core.Behavior)
   */
  @Generated
  @ImportedCapacityFeature(Behaviors.class)
  protected Behavior registerBehavior(final Behavior attitude) {
    return getSkill(io.sarl.core.Behaviors.class).registerBehavior(attitude);
  }
  
  /**
   * See the capacity {@link io.sarl.core.Behaviors#unregisterBehavior(io.sarl.lang.core.Behavior)}.
   * 
   * @see io.sarl.core.Behaviors#unregisterBehavior(io.sarl.lang.core.Behavior)
   */
  @Generated
  @ImportedCapacityFeature(Behaviors.class)
  protected Behavior unregisterBehavior(final Behavior attitude) {
    return getSkill(io.sarl.core.Behaviors.class).unregisterBehavior(attitude);
  }
  
  /**
   * See the capacity {@link io.sarl.core.Behaviors#wake(io.sarl.lang.core.Event)}.
   * 
   * @see io.sarl.core.Behaviors#wake(io.sarl.lang.core.Event)
   */
  @Generated
  @ImportedCapacityFeature(Behaviors.class)
  protected void wake(final Event evt) {
    getSkill(io.sarl.core.Behaviors.class).wake(evt);
  }
  
  @DefaultValueUse("org.arakhne.afc.math.continous.object2d.Vector2f,boolean")
  @Generated
  public final void move(final Vector2f direction) {
    move(direction, io.sarl.jaak.environment.external.PhysicBody.___FORMAL_PARAMETER_DEFAULT_VALUE_MOVE_1);
  }
  
  /**
   * Construct a skill.
   * @param owner - agent that is owning this skill.
   */
  @Generated
  public PhysicBodySkill(final Agent owner) {
    super(owner);
  }
  
  /**
   * Construct a skill. The owning agent is unknown.
   */
  @Generated
  public PhysicBodySkill() {
    super();
  }
}
