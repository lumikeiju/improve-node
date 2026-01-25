// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.improvenode;

import java.util.Collection;
import java.util.stream.Collectors;

import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;

/**
 * Static utility class containing helper methods for node improvement operations.
 * Provides validation, selection, and geometric calculation utilities.
 *
 * @author Lumikeiju, 2026
 */
final class ImproveNodeAccuracyHelper {

    private ImproveNodeAccuracyHelper() {
        // Hide default constructor for utils classes
    }

    /**
     * Checks if a node is valid for improvement operations.
     * A node is valid if it is not deleted, is selectable, and has valid coordinates.
     *
     * @param node the node to validate
     * @return true if the node can be improved, false otherwise
     */
    public static boolean isNodeValidForImprovement(Node node) {
        if (node == null || node.isDeleted() || !node.isSelectable()) {
            return false;
        }
        
        EastNorth coords = node.getEastNorth();
        return coords != null;
    }

    /**
     * Gets all selected nodes from the dataset that are valid for improvement.
     *
     * @param dataSet the current dataset
     * @return collection of valid selected nodes
     */
    public static Collection<Node> getValidSelectedNodes(DataSet dataSet) {
        if (dataSet == null) {
            return null;
        }
        
        return dataSet.getSelectedNodes().stream()
                .filter(ImproveNodeAccuracyHelper::isNodeValidForImprovement)
                .collect(Collectors.toList());
    }

    // TODO: Add geometric helper methods as needed for:
    // - Calculating perpendicular distances
    // - Computing angles between points
    // - Finding nearest points on lines
    // - Other visualization helpers
}
