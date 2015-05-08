package io.sarl.jaak.demos.ants.behaviors;

import com.google.common.base.Objects;
import io.sarl.jaak.demos.ants.behaviors.PheromoneFollowingCapacity;
import io.sarl.jaak.demos.ants.environment.Pheromone;
import io.sarl.lang.annotation.Generated;
import io.sarl.lang.core.Agent;
import io.sarl.lang.core.Skill;
import org.arakhne.afc.math.continous.object2d.Point2f;

/**
 * The skill to select a route of pheromone.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class PheromoneFollowingSkill extends Skill implements PheromoneFollowingCapacity {
  public Pheromone followPheromone(final Point2f position, final Iterable<? extends Pheromone> pheromones) {
    float minAmount = Float.POSITIVE_INFINITY;
    Pheromone currentP = null;
    Pheromone minP = null;
    for (final Pheromone p : pheromones) {
      {
        Point2f pp = p.getPosition();
        boolean _notEquals = (!Objects.equal(pp, position));
        if (_notEquals) {
          boolean _or = false;
          if ((minP == null)) {
            _or = true;
          } else {
            float _floatValue = p.floatValue();
            boolean _lessThan = (_floatValue < minAmount);
            _or = _lessThan;
          }
          if (_or) {
            minP = p;
            float _floatValue_1 = p.floatValue();
            minAmount = _floatValue_1;
          }
        } else {
          currentP = p;
        }
      }
    }
    boolean _or = false;
    if ((currentP == null)) {
      _or = true;
    } else {
      boolean _and = false;
      if (!(minP != null)) {
        _and = false;
      } else {
        float _floatValue = currentP.floatValue();
        boolean _greaterEqualsThan = (_floatValue >= minAmount);
        _and = _greaterEqualsThan;
      }
      _or = _and;
    }
    if (_or) {
      return minP;
    }
    return null;
  }
  
  /**
   * Construct a skill.
   * @param owner - agent that is owning this skill.
   */
  @Generated
  public PheromoneFollowingSkill(final Agent owner) {
    super(owner);
  }
  
  /**
   * Construct a skill. The owning agent is unknown.
   */
  @Generated
  public PheromoneFollowingSkill() {
    super();
  }
}
