package io.sarl.jaak.demos.ants;

import io.sarl.core.Initialize;
import io.sarl.jaak.demos.ants.Ant;
import io.sarl.jaak.demos.ants.AntColonyConstants;
import io.sarl.jaak.demos.ants.environment.AntColony;
import io.sarl.jaak.demos.ants.environment.Food;
import io.sarl.jaak.demos.ants.spawn.AntColonySpawner;
import io.sarl.jaak.demos.ants.ui.AntFrame;
import io.sarl.jaak.demos.ants.ui.AntPanel;
import io.sarl.jaak.environment.internal.model.JaakEnvironment;
import io.sarl.jaak.environment.internal.solver.ActionApplier;
import io.sarl.jaak.environment.internal.spawner.JaakSpawner;
import io.sarl.jaak.kernel.internal.JaakKernelAgent;
import io.sarl.jaak.util.RandomNumber;
import io.sarl.lang.annotation.Generated;
import io.sarl.lang.core.Agent;
import io.sarl.lang.core.Percept;
import java.util.Comparator;
import java.util.Set;
import java.util.UUID;
import org.arakhne.afc.math.continous.object2d.Point2f;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;

/**
 * This class defines the simulation environment for the ant colony problem.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class AntColonyProblem extends JaakKernelAgent {
  protected boolean isWrappedEnvironment = true;
  
  protected final Set<Point2f> positions = CollectionLiterals.<Point2f>newTreeSet(
    new Comparator<Point2f>() {
      public int compare(final Point2f o1, final Point2f o2) {
        if ((o1 == o2)) {
          return 0;
        }
        if ((o1 == null)) {
          return Integer.MIN_VALUE;
        }
        if ((o2 == null)) {
          return Integer.MAX_VALUE;
        }
        int _x = o1.x();
        int _x_1 = o2.x();
        int cmp = (_x - _x_1);
        if ((cmp != 0)) {
          return cmp;
        }
        int _y = o1.y();
        int _y_1 = o2.y();
        return (_y - _y_1);
      }
    });
  
  @Percept
  public void _handle_Initialize_0(final Initialize occurrence) {
    super._handle_Initialize_0(occurrence);
    AntPanel uiPanel = new AntPanel();
    this.addJaakListener(uiPanel);
    AntFrame uiFrame = new AntFrame(uiPanel, AntColonyConstants.WIDTH, AntColonyConstants.HEIGHT, this.controller);
    this.addJaakListener(uiFrame);
    this.fireEnvironmentChange();
    uiFrame.setVisible(true);
    this.controller.startSimulation();
  }
  
  /**
   * Create an ant colony and the associated spawner.
   * 
   * @param colonyId is the identifier of the colony to create.
   * @return an instance of the spawner.
   */
  public JaakSpawner createColony(final int colonyId) {
    ActionApplier actionApplier = this.physicEnvironment.getActionApplier();
    float _nextFloat = RandomNumber.nextFloat();
    float _width = this.physicEnvironment.getWidth();
    float _multiply = (_nextFloat * _width);
    float _nextFloat_1 = RandomNumber.nextFloat();
    float _height = this.physicEnvironment.getHeight();
    float _multiply_1 = (_nextFloat_1 * _height);
    Point2f position = new Point2f(_multiply, _multiply_1);
    while (this.positions.contains(position)) {
      float _nextFloat_2 = RandomNumber.nextFloat();
      float _width_1 = this.physicEnvironment.getWidth();
      float _multiply_2 = (_nextFloat_2 * _width_1);
      float _nextFloat_3 = RandomNumber.nextFloat();
      float _height_1 = this.physicEnvironment.getHeight();
      float _multiply_3 = (_nextFloat_3 * _height_1);
      position.set(_multiply_2, _multiply_3);
    }
    this.positions.add(position);
    AntColony antColonyObject = new AntColony(colonyId);
    int _x = position.x();
    int _y = position.y();
    actionApplier.putObject(_x, _y, antColonyObject);
    int _x_1 = position.x();
    int _y_1 = position.y();
    return new AntColonySpawner(
      AntColonyConstants.ANT_COLONY_PATROLLER_POPULATION, 
      AntColonyConstants.ANT_COLONY_FORAGER_POPULATION, _x_1, _y_1);
  }
  
  /**
   * Create the spawners to put on the environment
   * at the start up of the simulation.
   * 
   * @return the start-up spawners.
   */
  public JaakSpawner[] createSpawners() {
    JaakSpawner[] spawners = new JaakSpawner[AntColonyConstants.ANT_COLONY_COUNT];
    for (int i = 0; (i < spawners.length); i++) {
      JaakSpawner _createColony = this.createColony((i + 1));
      spawners[i] = _createColony;
    }
    return spawners;
  }
  
  /**
   * Create an instance of the environment that must
   * be used by the Jaak kernel.
   * 
   * @return the instance of time manager.
   */
  public JaakEnvironment createEnvironment() {
    JaakEnvironment environment = new JaakEnvironment(AntColonyConstants.WIDTH, AntColonyConstants.HEIGHT);
    environment.setWrapped(this.isWrappedEnvironment);
    ActionApplier actionApplier = environment.getActionApplier();
    for (int i = 0; (i < AntColonyConstants.FOOD_SOURCES); i++) {
      {
        float _nextFloat = RandomNumber.nextFloat();
        float _width = environment.getWidth();
        float _multiply = (_nextFloat * _width);
        float _nextFloat_1 = RandomNumber.nextFloat();
        float _height = environment.getHeight();
        float _multiply_1 = (_nextFloat_1 * _height);
        Point2f p = new Point2f(_multiply, _multiply_1);
        while (this.positions.contains(p)) {
          float _nextFloat_2 = RandomNumber.nextFloat();
          float _width_1 = environment.getWidth();
          float _multiply_2 = (_nextFloat_2 * _width_1);
          float _nextFloat_3 = RandomNumber.nextFloat();
          float _height_1 = environment.getHeight();
          float _multiply_3 = (_nextFloat_3 * _height_1);
          p.set(_multiply_2, _multiply_3);
        }
        this.positions.add(p);
        int _nextInt = RandomNumber.nextInt(AntColonyConstants.MAX_FOOD_PER_SOURCE);
        int _max = Math.max(10, _nextInt);
        Food food = new Food(_max);
        int _x = p.x();
        int _y = p.y();
        actionApplier.putObject(_x, _y, food);
      }
    }
    return environment;
  }
  
  /**
   * Replies the type of the agents to spawn.
   * 
   * @param spawner - the spawner that will create the agent.
   */
  public Class<? extends Agent> getSpawnableAgentType(final JaakSpawner spawner) {
    return Ant.class;
  }
  
  /**
   * Construct an agent.
   * @param parentID - identifier of the parent. It is the identifier of the parent agent and the enclosing contect, at the same time.
   */
  @Generated
  public AntColonyProblem(final UUID parentID) {
    super(parentID, null);
  }
  
  /**
   * Construct an agent.
   * @param parentID - identifier of the parent. It is the identifier of the parent agent and the enclosing contect, at the same time.
   * @param agentID - identifier of the agent. If <code>null</code> the agent identifier will be computed randomly.
   */
  @Generated
  public AntColonyProblem(final UUID parentID, final UUID agentID) {
    super(parentID, agentID);
  }
}
