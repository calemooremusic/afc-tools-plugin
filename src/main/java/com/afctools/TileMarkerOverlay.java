package com.afctools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class TileMarkerOverlay extends Overlay
{
    private final Client client;
    private final AfcToolsConfig config;

    private static final Color MARKER_COLOR = new Color(255, 0, 250, 255);

    // CHANGED TO PUBLIC SO PK LOG MANAGER CAN READ IT
    public static final Map<WorldPoint, String> COURSE_TILES = new HashMap<>();

    static
    {
        COURSE_TILES.put(WorldPoint.fromRegion(11837, 54, 27, 0), "DD");
        COURSE_TILES.put(WorldPoint.fromRegion(11837, 61, 32, 0), "Disp");
        COURSE_TILES.put(WorldPoint.fromRegion(11837, 54, 20, 0), "Plank");
        COURSE_TILES.put(WorldPoint.fromRegion(11837, 54, 9, 0), "Multi");
        COURSE_TILES.put(WorldPoint.fromRegion(11837, 57, 19, 0), "Slip");
        COURSE_TILES.put(WorldPoint.fromRegion(11837, 61, 61, 0), "Ladder");
        COURSE_TILES.put(WorldPoint.fromRegion(11837, 62, 40, 0), "Pipe");
        COURSE_TILES.put(WorldPoint.fromRegion(11837, 49, 36, 0), "Rocks");
        COURSE_TILES.put(WorldPoint.fromRegion(11837, 56, 46, 0), "Log");
        COURSE_TILES.put(WorldPoint.fromRegion(11837, 49, 56, 0), "Lava");
        COURSE_TILES.put(WorldPoint.fromRegion(12093, 0, 51, 0), "Rope");
        COURSE_TILES.put(WorldPoint.fromRegion(11937, 59, 48, 0), "Pit");
    }

    @Inject
    private TileMarkerOverlay(Client client, AfcToolsConfig config)
    {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.tileMarkerEnabled())
        {
            return null;
        }

        for (Map.Entry<WorldPoint, String> entry : COURSE_TILES.entrySet())
        {
            renderTile(graphics, entry.getKey(), entry.getValue());
        }

        return null;
    }

    private void renderTile(Graphics2D graphics, WorldPoint point, String label)
    {
        if (client.getLocalPlayer() == null) return;

        LocalPoint lp = LocalPoint.fromWorld(client, point);

        if (lp != null)
        {
            Polygon poly = Perspective.getCanvasTilePoly(client, lp);
            if (poly != null)
            {
                OverlayUtil.renderPolygon(graphics, poly, MARKER_COLOR);
            }

            Point textLocation = Perspective.getCanvasTextLocation(client, graphics, lp, label, 0);
            if (textLocation != null)
            {
                OverlayUtil.renderTextLocation(graphics, textLocation, label, Color.WHITE);
            }
        }
    }
}