package com.example.engine

import com.example.data.model.KnowledgeItem

object CricketKnowledgeBank {
    val items: List<KnowledgeItem> = listOf(
        // Basic Rules
        KnowledgeItem(
            id = "rule_pitch_dimensions",
            title = "Pitch Dimensions & Creases",
            category = "Basic Rules",
            summary = "The central playing strip is 22 yards (20.12m) long between wickets with four key creases.",
            fullExplanation = "The cricket pitch is a rectangular area 22 yards (20.12 metres) long and 10 feet (3.05 metres) wide. At each end are three stumps (28 inches/71.1 cm high) topped with two bails, totaling 9 inches (22.86 cm) wide.\n\nCreases:\n1. Bowling Crease: The line through the stumps (8 ft 8 in / 2.64 m).\n2. Popping Crease: 4 feet (1.22 m) in front of the bowling crease. The batter is safe inside this line; the bowler's front foot must land with some part behind it.\n3. Return Creases: Perpendicular lines on both sides of the pitch, 4 ft 4 in (1.32 m) either side of the wicket.",
            ruleReference = "MCC Law 6 & Law 7",
            example = "If a bowler's front foot has no part grounded or raised behind the popping crease on landing, an umpire signals No-ball.",
            tags = listOf("Pitch", "Crease", "Dimensions", "Stumps")
        ),
        KnowledgeItem(
            id = "rule_runs_boundaries",
            title = "Runs & Boundaries (Fours and Sixes)",
            category = "Basic Rules",
            summary = "Runs are scored by running between creases, hitting boundaries (4 or 6), or through extras.",
            fullExplanation = "Batters score by running between popping creases after hitting the delivery. Both batters must cross and ground their bat or person past the opposite popping crease for one run.\n\nBoundaries:\n- Four (4 runs): When the ball touches or crosses the boundary rope having touched the ground along the way.\n- Six (6 runs): When the ball crosses or lands outside the boundary on the full without touching the playing area.\n- Overthrows: If a fielder throws the ball and it goes past fielders or crosses the boundary, additional runs are counted.",
            ruleReference = "MCC Law 18 & Law 19",
            example = "If the batters have completed 1 run and an overthrow hits the boundary rope, 5 runs total are awarded.",
            tags = listOf("Runs", "Boundary", "Four", "Six", "Overthrow")
        ),
        KnowledgeItem(
            id = "rule_overs_innings",
            title = "Overs & Innings Structure",
            category = "Basic Rules",
            summary = "An over consists of 6 legal deliveries bowled from one end of the pitch.",
            fullExplanation = "A standard over comprises six valid, legal deliveries bowled by one bowler. At the completion of each over, the umpires call 'Over', field positions invert ends, and a different bowler bowls from the other wicket.\n\nWides and no-balls do not count toward the six legal deliveries; an extra delivery must be bowled in their place.\n\nIn limited-overs matches (e.g. T20, ODI), each side bats for a maximum allocated number of overs unless bowled out earlier.",
            ruleReference = "MCC Law 17",
            example = "If a bowler delivers 1 wide and 1 no-ball in an over, they will deliver 8 balls total before the 6 legal balls are completed.",
            tags = listOf("Over", "Innings", "Legal Delivery")
        ),

        // Dismissals
        KnowledgeItem(
            id = "dismissal_bowled",
            title = "Bowled",
            category = "Dismissals",
            summary = "The ball is bowled and breaks the striker's wicket directly or off the bat/pad.",
            fullExplanation = "Under MCC Law 32, the striker is out Bowled if their wicket is put down by a ball delivered by the bowler, not being a No-ball. This applies even if the ball first hits the batter's bat or person before dislodging the bails.\n\nCredited to the bowler: YES.",
            ruleReference = "MCC Law 32",
            example = "An inswinging delivery edges off the inside of the bat and knocks off the off-bail.",
            tags = listOf("Dismissal", "Bowled", "Wicket")
        ),
        KnowledgeItem(
            id = "dismissal_caught",
            title = "Caught (Catch Out)",
            category = "Dismissals",
            summary = "The ball touches the bat or gloved hand and is held cleanly by a fielder without touching ground.",
            fullExplanation = "A batter is out Caught if a legal delivery touches the bat (or the hand holding the bat) and is caught cleanly by a fielder within the boundary before touching the ground.\n\nIf the ball is a No-ball, the batter cannot be out Caught (unless caught out under Obstructing the field).\n\nCredited to the bowler: YES.",
            ruleReference = "MCC Law 33",
            example = "An edge to first slip or a skied lofted drive caught at long-on.",
            tags = listOf("Dismissal", "Caught", "Fielding")
        ),
        KnowledgeItem(
            id = "dismissal_lbw",
            title = "Leg Before Wicket (LBW)",
            category = "Dismissals",
            summary = "The ball hits the batter's person preventing it from hitting the wicket under strict conditions.",
            fullExplanation = "For an LBW dismissal under MCC Law 36, all of the following conditions must be satisfied on a legal delivery:\n1. The ball must NOT pitch outside the line of leg stump.\n2. The ball must either pitch in line with the wickets OR pitch outside off stump.\n3. The ball must strike the batter's person (not bat) in line with the wickets (or outside off stump ONLY IF no genuine stroke was offered).\n4. The ball would have gone on to hit the stumps.\n\nCredited to the bowler: YES.",
            ruleReference = "MCC Law 36",
            example = "A ball pitching in line with middle stump strikes the front pad in front of middle stump; tracking shows it was hitting leg stump.",
            tags = listOf("Dismissal", "LBW", "Hawk-Eye", "Pad")
        ),
        KnowledgeItem(
            id = "dismissal_run_out",
            title = "Run Out",
            category = "Dismissals",
            summary = "A wicket is broken by a fielder while the batter is out of their ground during a run attempt.",
            fullExplanation = "A batter is Run Out if they are out of their ground while the ball is in play and their wicket is fairly broken by the fielding side.\n\nKey Rule: A run-out is NOT credited to the bowler in bowling statistics. It is credited to the fielder(s) involved.\n\nCredited to the bowler: NO.",
            ruleReference = "MCC Law 38",
            example = "Direct hit from mid-wicket while the non-striker tries to complete a second run.",
            tags = listOf("Dismissal", "Run Out", "Fielding")
        ),
        KnowledgeItem(
            id = "dismissal_stumped",
            title = "Stumped",
            category = "Dismissals",
            summary = "The wicketkeeper breaks the wicket while the striker is out of their ground without attempting a run.",
            fullExplanation = "A batter is Stumped if they step out of their popping crease to play a delivery (not a No-ball), miss the ball, and the wicketkeeper breaks the wicket with the ball in hand before the batter regains ground.\n\nCredited to the bowler: YES.",
            ruleReference = "MCC Law 39",
            example = "A spinner beats the batter in flight; the batter's back foot is in the air over the crease as the keeper removes bails.",
            tags = listOf("Dismissal", "Stumped", "Wicketkeeper")
        ),
        KnowledgeItem(
            id = "dismissal_hit_wicket",
            title = "Hit Wicket",
            category = "Dismissals",
            summary = "The striker dislodges their own bails with bat or person while playing a shot or setting off for a run.",
            fullExplanation = "Under MCC Law 35, the striker is out Hit Wicket if their wicket is broken by their bat or any part of their person or equipment while preparing to receive, receiving a delivery, or setting off for the immediate first run.\n\nCredited to the bowler: YES.",
            ruleReference = "MCC Law 35",
            example = "A batter rocks onto the back foot against a fast bouncer and their back heel clips the leg stump bail.",
            tags = listOf("Dismissal", "Hit Wicket")
        ),

        // Extras
        KnowledgeItem(
            id = "extras_wide_ball",
            title = "Wide Ball",
            category = "Extras",
            summary = "A delivery judged too far from the striker to be hit with a normal cricket stroke.",
            fullExplanation = "If the bowler bowls the ball so high or wide of the wicket that it passes out of the reach of the striker playing a normal cricket stroke, the umpire signals a Wide.\n\nConsequences:\n- 1 penalty run is scored as an extra to the batting team (and counted against the bowler's runs conceded).\n- The ball does NOT count as a legal delivery in the over; it must be re-bowled.",
            ruleReference = "MCC Law 22",
            example = "A ball passing outside the return crease or wide tramlines in limited-overs matches.",
            tags = listOf("Extras", "Wide", "Penalty")
        ),
        KnowledgeItem(
            id = "extras_no_ball",
            title = "No-Ball & Free Hit",
            category = "Extras",
            summary = "An illegal delivery due to bowler foot placement, dangerous bowling, or height above waist.",
            fullExplanation = "A delivery is called a No-ball if:\n1. Front-foot: No part of the front foot is grounded behind the popping crease.\n2. Back-foot: Back foot touches or cuts the return crease.\n3. Above waist: A full toss (beamer) passes the batter above waist height in normal stance.\n4. Throwing: Illegal arm extension exceeding 15 degrees.\n\nIn T20 & ODI cricket, any No-ball awards a Free Hit on the next delivery, where the batter cannot be dismissed by bowler modes (only run out, obstructing, or hit ball twice).",
            ruleReference = "MCC Law 21 & ICC Playing Conditions",
            example = "Overstepping the popping crease: 1 run extra, delivery re-bowled, and free hit on next ball.",
            tags = listOf("Extras", "No-Ball", "Free Hit")
        ),
        KnowledgeItem(
            id = "extras_byes_leg_byes",
            title = "Byes & Leg-Byes",
            category = "Extras",
            summary = "Runs scored when the ball misses the bat (byes) or deflects off the batter's person (leg-byes).",
            fullExplanation = "Byes: Runs scored when a legal ball passes the batter without touching the bat or body.\nLeg-byes: Runs scored when the ball deflects off the batter's body/pads, provided the batter either attempted to play the ball with the bat or tried to avoid being hit.\n\nVital Statistical Distinction:\n- Both count as legal deliveries toward the over.\n- Byes and leg-byes are credited to Team Extras, NOT charged against the bowler's personal bowling runs conceded in career statistics!",
            ruleReference = "MCC Law 23",
            example = "A ball beats the bat and keeper to roll for 4 byes: team gets 4 extras, bowler's economy is NOT penalized.",
            tags = listOf("Extras", "Byes", "Leg-Byes")
        ),

        // Match Formats
        KnowledgeItem(
            id = "format_test_cricket",
            title = "Test Cricket (First-Class)",
            category = "Formats",
            summary = "The traditional 5-day format with two innings per side and red/pink ball.",
            fullExplanation = "Test cricket represents the pinnacle of endurance and tactical skill:\n- Duration: Up to 5 days, 3 sessions per day (approx 90 overs/day).\n- Two innings per side: A team must take 20 wickets to win.\n- White clothing and red ball (or pink ball for Day/Night Tests).\n- Follow-on: If Team B scores 200 or more runs fewer than Team A in the first innings, Team A can enforce the follow-on.",
            ruleReference = "MCC Laws & ICC Test Playing Conditions",
            example = "Enforcing a follow-on when leading by 250 runs in the first innings.",
            tags = listOf("Test Cricket", "Follow-on", "Red Ball")
        ),
        KnowledgeItem(
            id = "format_limited_overs",
            title = "ODI, T20 & T10 Cricket",
            category = "Formats",
            summary = "Fast-paced limited-overs cricket formats with maximum overs and field restrictions.",
            fullExplanation = "Limited overs cricket requires high scoring rates:\n- ODI (One Day International): 50 overs per side, white ball, max 10 overs per bowler, powerplays in 3 phases.\n- T20: 20 overs per side, max 4 overs per bowler, 6-over mandatory powerplay (max 2 fielders outside 30-yard circle).\n- T10: 10 overs per side, max 2 overs per bowler, powerplay of 3 overs.\n- Gully / Club: Custom overs agreed upon (e.g., 6, 8, 12, or 16 overs).",
            ruleReference = "ICC ODI & T20 Playing Conditions",
            example = "In a T20 match, a bowler can bowl a maximum of 4 overs (24 legal balls).",
            tags = listOf("ODI", "T20", "T10", "Limited Overs")
        ),

        // Terminology
        KnowledgeItem(
            id = "term_strike_rate_economy",
            title = "Strike Rate & Economy Rate",
            category = "Terminology",
            summary = "The definitive metrics measuring scoring velocity and bowling containment.",
            fullExplanation = "Batting Strike Rate (SR):\nFormula: (Runs Scored / Balls Faced) × 100\nIndicates runs scored per 100 balls. An SR of 150.00 means 1.5 runs per ball.\n\nBowling Economy Rate (Econ):\nFormula: Runs Conceded / Overs Bowled (or Runs / (Legal Balls / 6.0))\nIndicates average runs conceded per 6-ball over. An economy under 6.00 in T20 is elite.\n\nBowling Strike Rate:\nFormula: Legal Balls Bowled / Wickets Taken\nIndicates how many balls a bowler takes on average between each wicket.",
            ruleReference = "Cricket Statistical Standards",
            example = "45 runs from 30 balls = 150.00 strike rate. 26 runs from 4.2 overs (26 balls) = 6.00 economy.",
            tags = listOf("Strike Rate", "Economy", "Bowling Average", "Metrics")
        ),
        KnowledgeItem(
            id = "term_deliveries_art",
            title = "Yorker, Bouncer, Swing & Spin",
            category = "Terminology",
            summary = "Key bowling weapons: Yorker, Bouncer, Reverse Swing, Googly, and Doosra.",
            fullExplanation = "Key delivery types:\n- Yorker: A delivery pitching right at the batter's toes or popping crease, extremely hard to hit.\n- Bouncer: A short-pitched delivery bouncing chest or head high to push the batter back.\n- Conventional Swing: Lateral movement through the air caused by seam orientation and smooth/rough ball sides.\n- Reverse Swing: Late inward movement with an older, roughened ball.\n- Googly / Wrong'un: A leg-spinner's delivery that turns in the opposite direction (into the right-handed batter).\n- Doosra: An off-spinner's delivery that turns away from the right-hander.",
            ruleReference = "Cricket Craft & Terminology",
            example = "A toe-crushing yorker bowled at the death overs targeting base of off-stump.",
            tags = listOf("Yorker", "Bouncer", "Swing", "Spin", "Googly")
        ),

        // Advanced
        KnowledgeItem(
            id = "adv_drs_technology",
            title = "Decision Review System (DRS)",
            category = "Advanced",
            summary = "Technology suite using Hawk-Eye Ball Tracking, UltraEdge sound, and thermal HotSpot.",
            fullExplanation = "Teams are granted player reviews per innings against on-field umpire verdicts:\n- Ball Tracking (Hawk-Eye): Models ball trajectory from bowler's hand to predict whether it would hit the wickets.\n- UltraEdge / Snicko: Audio spike synchronization to confirm if ball edged the bat before hitting pad or caught.\n- Umpire's Call: When the ball tracking shows marginal contact (less than 50% of the ball hitting stump or zone), the on-field decision stands and the team does NOT lose their review.",
            ruleReference = "ICC Playing Conditions Appendix",
            example = "Reviewing an on-field 'Not Out' LBW call; ball tracking confirms ball pitching in line, hitting in line, and hitting stumps to overturn the decision to OUT.",
            tags = listOf("DRS", "Ball Tracking", "UltraEdge", "Umpire's Call")
        ),
        KnowledgeItem(
            id = "adv_umpire_signals",
            title = "Official Umpire Signals",
            category = "Advanced",
            summary = "Standard physical gestures used by umpires to communicate match events to players and scorers.",
            fullExplanation = "Core Signals:\n- Out: Raising index finger above head.\n- Not Out: Shaking head or sweeping hands downward.\n- Boundary Four: Waving arm side to side across chest.\n- Boundary Six: Raising both arms above head.\n- Wide: Extending both arms horizontally sideways.\n- No-ball: Extending one arm horizontally to side.\n- Leg-bye: Touching raised knee with hand.\n- Bye: Raising open hand vertically above head.\n- Dead Ball: Crossing wrists across chest.\n- Free Hit: Circling finger above head.",
            ruleReference = "MCC Law 2",
            example = "Umpire raises both arms high to confirm a clean six over long-on.",
            tags = listOf("Signals", "Umpire", "Out", "Six", "Free Hit")
        )
    )
}
