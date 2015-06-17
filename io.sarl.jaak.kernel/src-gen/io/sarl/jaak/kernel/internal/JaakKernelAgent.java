package io.sarl.jaak.kernel.internal;

import com.google.common.base.Objects;
import io.sarl.core.AgentKilled;
import io.sarl.core.AgentSpawned;
import io.sarl.core.AgentTask;
import io.sarl.core.Behaviors;
import io.sarl.core.DefaultContextInteractions;
import io.sarl.core.Destroy;
import io.sarl.core.Initialize;
import io.sarl.core.Lifecycle;
import io.sarl.core.Schedules;
import io.sarl.jaak.environment.external.BodyCreated;
import io.sarl.jaak.environment.external.Perception;
import io.sarl.jaak.environment.external.SimulationStarted;
import io.sarl.jaak.environment.external.SimulationStopped;
import io.sarl.jaak.environment.external.body.BodySpawner;
import io.sarl.jaak.environment.external.body.TurtleBody;
import io.sarl.jaak.environment.external.body.TurtleBodyFactory;
import io.sarl.jaak.environment.external.body.TurtleObject;
import io.sarl.jaak.environment.external.time.TimeManager;
import io.sarl.jaak.environment.internal.model.JaakEnvironment;
import io.sarl.jaak.environment.internal.spawner.JaakSpawner;
import io.sarl.jaak.environment.internal.spawner.JaakWorldSpawner;
import io.sarl.jaak.kernel.external.JaakController;
import io.sarl.jaak.kernel.external.JaakEvent;
import io.sarl.jaak.kernel.external.JaakListener;
import io.sarl.jaak.kernel.external.JaakPhysicSpace;
import io.sarl.jaak.kernel.external.JaakPhysicSpaceConstants;
import io.sarl.jaak.kernel.internal.AbstractStampedEvent;
import io.sarl.jaak.kernel.internal.AgentBodyCreator;
import io.sarl.jaak.kernel.internal.AgentInfluence;
import io.sarl.jaak.kernel.internal.DefaultJaakTimeManager;
import io.sarl.jaak.kernel.internal.ExecuteSimulationStep;
import io.sarl.jaak.kernel.internal.JaakKernelController;
import io.sarl.jaak.kernel.internal.JaakPhysicSpaceSpecification;
import io.sarl.jaak.kernel.internal.SynchronizeBody;
import io.sarl.jaak.kernel.internal.TurtleCreated;
import io.sarl.jaak.kernel.internal.TurtleDestroyed;
import io.sarl.lang.annotation.EarlyExit;
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
import io.sarl.lang.core.Percept;
import io.sarl.lang.core.Scope;
import io.sarl.lang.core.Space;
import io.sarl.lang.core.SpaceID;
import io.sarl.util.Scopes;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * Provide the core agent which is responsible of the Jaak environment.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class JaakKernelAgent extends Agent {
  protected final List<JaakListener> environmentListeners = CollectionLiterals.<JaakListener>newArrayList();
  
  protected final List<UUID> removedAgents = CollectionLiterals.<UUID>newLinkedList();
  
  protected final Map<UUID, TurtleCreated> addedAgents = CollectionLiterals.<UUID, TurtleCreated>newTreeMap(null);
  
  protected final AtomicBoolean isWaitingInfluences = new AtomicBoolean(false);
  
  protected long receivedInfluences = 0;
  
  protected final JaakController controller = new JaakKernelController();
  
  protected JaakPhysicSpace physicSpace;
  
  protected Address defaultAddressInPhysicSpace;
  
  protected JaakEnvironment physicEnvironment;
  
  protected TimeManager timeManager;
  
  protected JaakSpawner[] spawners;
  
  protected JaakWorldSpawner defaultSpawner;
  
  protected AgentTask waitingTask;
  
  public void addJaakListener(final JaakListener listener) {
    /* this.environmentListeners; */
    synchronized (this.environmentListeners) {
      this.environmentListeners.add(listener);
    }
  }
  
  public void removeJaakListener(final JaakListener listener) {
    /* this.environmentListeners; */
    synchronized (this.environmentListeners) {
      this.environmentListeners.remove(listener);
    }
  }
  
  public void fireSimulationStarted() {
    JaakListener[] list = null;
    /* this.environmentListeners; */
    synchronized (this.environmentListeners) {
      {
        int _size = this.environmentListeners.size();
        JaakListener[] _newArrayOfSize = new JaakListener[_size];
        list = _newArrayOfSize;
        this.environmentListeners.<JaakListener>toArray(list);
      }
    }
    Collection<BodySpawner> _unmodifiableCollection = Collections.<BodySpawner>unmodifiableCollection(((Collection<? extends BodySpawner>)Conversions.doWrapArray(this.spawners)));
    float _currentTime = this.timeManager.getCurrentTime();
    float _lastStepDuration = this.timeManager.getLastStepDuration();
    JaakEvent evt = new JaakEvent(this, 
      this.physicEnvironment, _unmodifiableCollection, _currentTime, _lastStepDuration);
    for (final JaakListener listener : list) {
      listener.simulationStarted(evt);
    }
  }
  
  public void fireSimulationStopped() {
    JaakListener[] list = null;
    /* this.environmentListeners; */
    synchronized (this.environmentListeners) {
      {
        int _size = this.environmentListeners.size();
        JaakListener[] _newArrayOfSize = new JaakListener[_size];
        list = _newArrayOfSize;
        this.environmentListeners.<JaakListener>toArray(list);
      }
    }
    Collection<BodySpawner> _unmodifiableCollection = Collections.<BodySpawner>unmodifiableCollection(((Collection<? extends BodySpawner>)Conversions.doWrapArray(this.spawners)));
    float _currentTime = this.timeManager.getCurrentTime();
    float _lastStepDuration = this.timeManager.getLastStepDuration();
    JaakEvent evt = new JaakEvent(this, 
      this.physicEnvironment, _unmodifiableCollection, _currentTime, _lastStepDuration);
    for (final JaakListener listener : list) {
      listener.simulationStopped(evt);
    }
  }
  
  public void fireEnvironmentChange() {
    JaakListener[] list = null;
    /* this.environmentListeners; */
    synchronized (this.environmentListeners) {
      {
        int _size = this.environmentListeners.size();
        JaakListener[] _newArrayOfSize = new JaakListener[_size];
        list = _newArrayOfSize;
        this.environmentListeners.<JaakListener>toArray(list);
      }
    }
    Collection<BodySpawner> _unmodifiableCollection = Collections.<BodySpawner>unmodifiableCollection(((Collection<? extends BodySpawner>)Conversions.doWrapArray(this.spawners)));
    float _currentTime = this.timeManager.getCurrentTime();
    float _lastStepDuration = this.timeManager.getLastStepDuration();
    JaakEvent evt = new JaakEvent(this, 
      this.physicEnvironment, _unmodifiableCollection, _currentTime, _lastStepDuration);
    for (final JaakListener listener : list) {
      listener.environmentStateChanged(evt);
    }
  }
  
  @Percept
  public void _handle_Initialize_0(final Initialize occurrence) {
    AgentContext _defaultContext = this.getDefaultContext();
    UUID spaceId = JaakPhysicSpaceConstants.getSpaceIDInContext(_defaultContext);
    AgentContext _defaultContext_1 = this.getDefaultContext();
    EventListener _asEventListener = this.asEventListener();
    JaakPhysicSpace _orCreateSpaceWithSpec = _defaultContext_1.<JaakPhysicSpace>getOrCreateSpaceWithSpec(JaakPhysicSpaceSpecification.class, spaceId, _asEventListener);
    this.physicSpace = _orCreateSpaceWithSpec;
    boolean _and = false;
    UUID _creatorID = this.physicSpace.getCreatorID();
    boolean _tripleNotEquals = (_creatorID != null);
    if (!_tripleNotEquals) {
      _and = false;
    } else {
      UUID _creatorID_1 = this.physicSpace.getCreatorID();
      UUID _iD = this.getID();
      boolean _notEquals = (!Objects.equal(_creatorID_1, _iD));
      _and = _notEquals;
    }
    if (_and) {
      this.killMe();
    } else {
      SpaceID _iD_1 = this.physicSpace.getID();
      UUID _iD_2 = this.getID();
      Address _address = new Address(_iD_1, _iD_2);
      this.defaultAddressInPhysicSpace = _address;
      TimeManager _createTimeManager = this.createTimeManager();
      this.timeManager = _createTimeManager;
      JaakEnvironment _createEnvironment = this.createEnvironment();
      this.physicEnvironment = _createEnvironment;
      this.physicEnvironment.setTimeManager(this.timeManager);
      JaakWorldSpawner _jaakWorldSpawner = new JaakWorldSpawner(this.physicEnvironment);
      this.defaultSpawner = _jaakWorldSpawner;
      JaakSpawner[] p = this.createSpawners();
      boolean _or = false;
      if ((p == null)) {
        _or = true;
      } else {
        final JaakSpawner[] _converted_p = (JaakSpawner[])p;
        boolean _isEmpty = ((List<JaakSpawner>)Conversions.doWrapArray(_converted_p)).isEmpty();
        _or = _isEmpty;
      }
      if (_or) {
        List<JaakSpawner> _singletonList = Collections.<JaakSpawner>singletonList(this.defaultSpawner);
        this.spawners = ((JaakSpawner[])Conversions.unwrapArray(_singletonList, JaakSpawner.class));
      } else {
        this.spawners = p;
      }
      EventSpace _defaultSpace = this.getDefaultSpace();
      Address _defaultAddress = this.getDefaultAddress();
      ((JaakKernelController) this.controller).initialize(_defaultSpace, _defaultAddress, 
        this.timeManager);
    }
  }
  
  @Generated
  private boolean _guard_Destroy_1(final Destroy occurrence) {
    return (this.physicEnvironment != null);
  }
  
  @Percept
  public void _handle_Destroy_1(final Destroy occurrence) {
    if (_guard_Destroy_1(occurrence)) {
      this.isWaitingInfluences.set(false);
      if ((this.waitingTask != null)) {
        this.cancel(this.waitingTask);
        this.waitingTask = null;
      }
      this.physicSpace.destroy();
      this.physicSpace = null;
      this.physicEnvironment = null;
    }
  }
  
  @Generated
  private boolean _guard_TurtleCreated_2(final TurtleCreated occurrence) {
    boolean _isValid = this.isValid(occurrence);
    return _isValid;
  }
  
  @Percept
  public void _handle_TurtleCreated_2(final TurtleCreated occurrence) {
    if (_guard_TurtleCreated_2(occurrence)) {
      /* this.addedAgents; */
      synchronized (this.addedAgents) {
        Address _source = occurrence.getSource();
        UUID _uUID = _source.getUUID();
        this.addedAgents.put(_uUID, occurrence);
      }
    }
  }
  
  @Generated
  private boolean _guard_TurtleDestroyed_3(final TurtleDestroyed occurrence) {
    boolean _isValid = this.isValid(occurrence);
    return _isValid;
  }
  
  @Percept
  public void _handle_TurtleDestroyed_3(final TurtleDestroyed occurrence) {
    if (_guard_TurtleDestroyed_3(occurrence)) {
      /* this.removedAgents; */
      synchronized (this.removedAgents) {
        Address _source = occurrence.getSource();
        UUID _uUID = _source.getUUID();
        this.removedAgents.add(_uUID);
      }
    }
  }
  
  @Generated
  private boolean _guard_AgentInfluence_4(final AgentInfluence occurrence) {
    boolean _isValid = this.isValid(occurrence);
    return _isValid;
  }
  
  @Percept
  public void _handle_AgentInfluence_4(final AgentInfluence occurrence) {
    if (_guard_AgentInfluence_4(occurrence)) {
      TurtleObject _emitter = occurrence.influence.getEmitter();
      UUID _turtleId = _emitter.getTurtleId();
      TurtleBody body = this.physicEnvironment.getBodyFor(_turtleId);
      if ((body != null)) {
        body.influence(occurrence.influence);
      }
    }
  }
  
  @Generated
  private boolean _guard_SynchronizeBody_5(final SynchronizeBody occurrence) {
    boolean _isValid2 = this.isValid2(occurrence);
    return _isValid2;
  }
  
  @Percept
  public void _handle_SynchronizeBody_5(final SynchronizeBody occurrence) {
    if (_guard_SynchronizeBody_5(occurrence)) {
      this.receivedInfluences++;
      int _turtleCount = this.physicEnvironment.getTurtleCount();
      boolean _greaterEqualsThan = (this.receivedInfluences >= _turtleCount);
      if (_greaterEqualsThan) {
        this.isWaitingInfluences.set(false);
        if ((this.waitingTask != null)) {
          this.cancel(this.waitingTask);
          this.waitingTask = null;
        }
        ((JaakKernelController) this.controller).wakeSimulator();
      }
    }
  }
  
  @Generated
  private boolean _guard_SimulationStarted_6(final SimulationStarted occurrence) {
    boolean _isFromMe = this.isFromMe(occurrence);
    return _isFromMe;
  }
  
  @Percept
  public void _handle_SimulationStarted_6(final SimulationStarted occurrence) {
    if (_guard_SimulationStarted_6(occurrence)) {
      this.runPreAgentExecution();
    }
  }
  
  @Generated
  private boolean _guard_ExecuteSimulationStep_7(final ExecuteSimulationStep occurrence) {
    boolean _isFromMe = this.isFromMe(occurrence);
    return _isFromMe;
  }
  
  @Percept
  public void _handle_ExecuteSimulationStep_7(final ExecuteSimulationStep occurrence) {
    if (_guard_ExecuteSimulationStep_7(occurrence)) {
      this.runPostAgentExecution();
      this.runPreAgentExecution();
    }
  }
  
  @Generated
  private boolean _guard_SimulationStopped_8(final SimulationStopped occurrence) {
    boolean _isFromMe = this.isFromMe(occurrence);
    return _isFromMe;
  }
  
  @Percept
  public void _handle_SimulationStopped_8(final SimulationStopped occurrence) {
    if (_guard_SimulationStopped_8(occurrence)) {
      this.killMe();
    }
  }
  
  public boolean isValid(final AbstractStampedEvent evt) {
    boolean _and = false;
    boolean _and_1 = false;
    boolean _and_2 = false;
    if (!(this.physicEnvironment != null)) {
      _and_2 = false;
    } else {
      Address _source = evt.getSource();
      SpaceID _spaceId = _source.getSpaceId();
      SpaceID _iD = this.physicSpace.getID();
      boolean _equals = Objects.equal(_spaceId, _iD);
      _and_2 = _equals;
    }
    if (!_and_2) {
      _and_1 = false;
    } else {
      boolean _get = this.isWaitingInfluences.get();
      _and_1 = _get;
    }
    if (!_and_1) {
      _and = false;
    } else {
      float _currentTime = this.timeManager.getCurrentTime();
      boolean _lessEqualsThan = (_currentTime <= evt.currentTime);
      _and = _lessEqualsThan;
    }
    return _and;
  }
  
  public boolean isValid2(final AbstractStampedEvent evt) {
    boolean _and = false;
    boolean _and_1 = false;
    if (!(this.physicEnvironment != null)) {
      _and_1 = false;
    } else {
      boolean _get = this.isWaitingInfluences.get();
      _and_1 = _get;
    }
    if (!_and_1) {
      _and = false;
    } else {
      float _currentTime = this.timeManager.getCurrentTime();
      boolean _lessEqualsThan = (_currentTime <= evt.currentTime);
      _and = _lessEqualsThan;
    }
    return _and;
  }
  
  /**
   * Run the tasks before the agent executions.
   */
  public void runPreAgentExecution() {
    if ((this.spawners != null)) {
      TurtleBodyFactory factory = this.physicEnvironment.getTurtleBodyFactory();
      for (final JaakSpawner spawner : this.spawners) {
        {
          UUID id = UUID.randomUUID();
          UUID _iD = this.getID();
          TurtleBody body = spawner.spawnBodyFor(id, _iD, factory, 
            this.timeManager, 
            null);
          if ((body != null)) {
            Class<? extends Agent> _spawnableAgentType = this.getSpawnableAgentType(spawner);
            AgentContext _defaultContext = this.getDefaultContext();
            this.spawnInContextWithID(_spawnableAgentType, id, _defaultContext);
            float _currentTime = this.timeManager.getCurrentTime();
            float _lastStepDuration = this.timeManager.getLastStepDuration();
            BodyCreated _bodyCreated = new BodyCreated(_currentTime, _lastStepDuration, body);
            EventSpace _defaultSpace = this.getDefaultSpace();
            SpaceID _iD_1 = _defaultSpace.getID();
            Address _address = new Address(_iD_1, id);
            Scope<Address> _addresses = Scopes.addresses(_address);
            this.emit(_bodyCreated, _addresses);
          }
        }
      }
      /* this.removedAgents; */
      synchronized (this.removedAgents) {
        {
          Iterator<UUID> iterator = this.removedAgents.iterator();
          while (iterator.hasNext()) {
            {
              UUID adr = iterator.next();
              iterator.remove();
              this.physicEnvironment.removeBodyFor(adr);
            }
          }
        }
      }
      /* this.addedAgents; */
      synchronized (this.addedAgents) {
        {
          Set<Map.Entry<UUID, TurtleCreated>> _entrySet = this.addedAgents.entrySet();
          Iterator<Map.Entry<UUID, TurtleCreated>> iterator = _entrySet.iterator();
          AgentBodyCreator creator = new AgentBodyCreator();
          while (iterator.hasNext()) {
            {
              Map.Entry<UUID, TurtleCreated> p = iterator.next();
              TurtleCreated _value = p.getValue();
              creator.set(_value);
              iterator.remove();
              UUID _key = p.getKey();
              UUID _iD = this.getID();
              this.defaultSpawner.spawnBodyFor(_key, _iD, factory, 
                this.timeManager, creator);
            }
          }
        }
      }
    }
    this.physicEnvironment.runPreTurtles();
    if ((this.waitingTask != null)) {
      this.cancel(this.waitingTask);
      this.waitingTask = null;
    }
    long _simulationStepTimeOut = this.controller.getSimulationStepTimeOut();
    final Procedure1<Agent> _function = new Procedure1<Agent>() {
      public void apply(final Agent it) {
        if ((JaakKernelAgent.this.waitingTask != null)) {
          JaakKernelAgent.this.cancel(JaakKernelAgent.this.waitingTask);
          JaakKernelAgent.this.waitingTask = null;
        }
        ((JaakKernelController) JaakKernelAgent.this.controller).wakeSimulator();
      }
    };
    AgentTask _in = this.in(_simulationStepTimeOut, _function);
    this.waitingTask = _in;
    this.receivedInfluences = 0;
    this.isWaitingInfluences.set(true);
    final JaakEnvironment.Lambda<TurtleBody> _function_1 = new JaakEnvironment.Lambda<TurtleBody>() {
      public void apply(final TurtleBody it) {
        float _currentTime = JaakKernelAgent.this.timeManager.getCurrentTime();
        float _lastStepDuration = JaakKernelAgent.this.timeManager.getLastStepDuration();
        Perception evt = new Perception(_currentTime, _lastStepDuration, it);
        evt.setSource(JaakKernelAgent.this.defaultAddressInPhysicSpace);
        JaakKernelAgent.this.physicSpace.notifyPerception(evt);
      }
    };
    this.physicEnvironment.apply(_function_1);
  }
  
  /**
   * Run the tasks after the agent executions.
   */
  public void runPostAgentExecution() {
    this.isWaitingInfluences.set(false);
    this.physicEnvironment.runPostTurtles();
    this.timeManager.increment();
    this.fireEnvironmentChange();
  }
  
  /**
   * Create an instance of the time manager that must be used by
   * the Jaak kernel.
   * 
   * @return the instance of time manager.
   */
  public TimeManager createTimeManager() {
    return new DefaultJaakTimeManager();
  }
  
  /**
   * Create the spawners to put on the environment
   * at the start up of the simulation.
   * 
   * @return the start-up spawners.
   */
  public JaakSpawner[] createSpawners() {
    return new JaakSpawner[] {};
  }
  
  /**
   * Create an instance of the environment that must
   * be used by the Jaak kernel.
   * 
   * @return the instance of time manager.
   */
  public JaakEnvironment createEnvironment() {
    throw new UnsupportedOperationException("must be overridden");
  }
  
  /**
   * Replies the type of the agents to spawn.
   * 
   * @param spawner - the spawner that will create the agent.
   */
  public Class<? extends Agent> getSpawnableAgentType(final JaakSpawner spawner) {
    throw new UnsupportedOperationException("must be overridden");
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
   * See the capacity {@link io.sarl.core.Lifecycle#killMe()}.
   * 
   * @see io.sarl.core.Lifecycle#killMe()
   */
  @EarlyExit
  @FiredEvent({ AgentKilled.class, Destroy.class })
  @Generated
  @ImportedCapacityFeature(Lifecycle.class)
  protected void killMe() {
    getSkill(io.sarl.core.Lifecycle.class).killMe();
  }
  
  /**
   * See the capacity {@link io.sarl.core.Lifecycle#spawnInContext(java.lang.Class<? extends io.sarl.lang.core.Agent>,io.sarl.lang.core.AgentContext,java.lang.Object[])}.
   * 
   * @see io.sarl.core.Lifecycle#spawnInContext(java.lang.Class<? extends io.sarl.lang.core.Agent>,io.sarl.lang.core.AgentContext,java.lang.Object[])
   */
  @FiredEvent(AgentSpawned.class)
  @Generated
  @ImportedCapacityFeature(Lifecycle.class)
  protected UUID spawnInContext(final Class<? extends Agent> agentClass, final AgentContext context, final Object... params) {
    return getSkill(io.sarl.core.Lifecycle.class).spawnInContext(agentClass, context, params);
  }
  
  /**
   * See the capacity {@link io.sarl.core.Lifecycle#spawnInContextWithID(java.lang.Class<? extends io.sarl.lang.core.Agent>,java.util.UUID,io.sarl.lang.core.AgentContext,java.lang.Object[])}.
   * 
   * @see io.sarl.core.Lifecycle#spawnInContextWithID(java.lang.Class<? extends io.sarl.lang.core.Agent>,java.util.UUID,io.sarl.lang.core.AgentContext,java.lang.Object[])
   */
  @FiredEvent(AgentSpawned.class)
  @Generated
  @ImportedCapacityFeature(Lifecycle.class)
  protected UUID spawnInContextWithID(final Class<? extends Agent> agentClass, final UUID agentID, final AgentContext context, final Object... params) {
    return getSkill(io.sarl.core.Lifecycle.class).spawnInContextWithID(agentClass, agentID, context, params);
  }
  
  /**
   * See the capacity {@link io.sarl.core.Schedules#cancel(io.sarl.core.AgentTask)}.
   * 
   * @see io.sarl.core.Schedules#cancel(io.sarl.core.AgentTask)
   */
  @Generated
  @ImportedCapacityFeature(Schedules.class)
  protected boolean cancel(final AgentTask task) {
    return getSkill(io.sarl.core.Schedules.class).cancel(task);
  }
  
  /**
   * See the capacity {@link io.sarl.core.Schedules#cancel(io.sarl.core.AgentTask,boolean)}.
   * 
   * @see io.sarl.core.Schedules#cancel(io.sarl.core.AgentTask,boolean)
   */
  @Generated
  @ImportedCapacityFeature(Schedules.class)
  protected boolean cancel(final AgentTask task, final boolean mayInterruptIfRunning) {
    return getSkill(io.sarl.core.Schedules.class).cancel(task, mayInterruptIfRunning);
  }
  
  /**
   * See the capacity {@link io.sarl.core.Schedules#every(long,(io.sarl.lang.core.Agent)=>void)}.
   * 
   * @see io.sarl.core.Schedules#every(long,(io.sarl.lang.core.Agent)=>void)
   */
  @Generated
  @ImportedCapacityFeature(Schedules.class)
  protected AgentTask every(final long period, final Procedure1<? super Agent> procedure) {
    return getSkill(io.sarl.core.Schedules.class).every(period, procedure);
  }
  
  /**
   * See the capacity {@link io.sarl.core.Schedules#every(io.sarl.core.AgentTask,long,(io.sarl.lang.core.Agent)=>void)}.
   * 
   * @see io.sarl.core.Schedules#every(io.sarl.core.AgentTask,long,(io.sarl.lang.core.Agent)=>void)
   */
  @Generated
  @ImportedCapacityFeature(Schedules.class)
  protected AgentTask every(final AgentTask task, final long period, final Procedure1<? super Agent> procedure) {
    return getSkill(io.sarl.core.Schedules.class).every(task, period, procedure);
  }
  
  /**
   * See the capacity {@link io.sarl.core.Schedules#in(long,(io.sarl.lang.core.Agent)=>void)}.
   * 
   * @see io.sarl.core.Schedules#in(long,(io.sarl.lang.core.Agent)=>void)
   */
  @Generated
  @ImportedCapacityFeature(Schedules.class)
  protected AgentTask in(final long delay, final Procedure1<? super Agent> procedure) {
    return getSkill(io.sarl.core.Schedules.class).in(delay, procedure);
  }
  
  /**
   * See the capacity {@link io.sarl.core.Schedules#in(io.sarl.core.AgentTask,long,(io.sarl.lang.core.Agent)=>void)}.
   * 
   * @see io.sarl.core.Schedules#in(io.sarl.core.AgentTask,long,(io.sarl.lang.core.Agent)=>void)
   */
  @Generated
  @ImportedCapacityFeature(Schedules.class)
  protected AgentTask in(final AgentTask task, final long delay, final Procedure1<? super Agent> procedure) {
    return getSkill(io.sarl.core.Schedules.class).in(task, delay, procedure);
  }
  
  /**
   * See the capacity {@link io.sarl.core.Schedules#task(java.lang.String)}.
   * 
   * @see io.sarl.core.Schedules#task(java.lang.String)
   */
  @Generated
  @ImportedCapacityFeature(Schedules.class)
  protected AgentTask task(final String name) {
    return getSkill(io.sarl.core.Schedules.class).task(name);
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
  
  /**
   * Construct an agent.
   * @param parentID - identifier of the parent. It is the identifier of the parent agent and the enclosing contect, at the same time.
   */
  @Generated
  public JaakKernelAgent(final UUID parentID) {
    super(parentID, null);
  }
  
  /**
   * Construct an agent.
   * @param parentID - identifier of the parent. It is the identifier of the parent agent and the enclosing contect, at the same time.
   * @param agentID - identifier of the agent. If <code>null</code> the agent identifier will be computed randomly.
   */
  @Generated
  public JaakKernelAgent(final UUID parentID, final UUID agentID) {
    super(parentID, agentID);
  }
}
