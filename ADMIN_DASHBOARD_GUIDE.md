# Admin Dashboard - Setup & User Guide

## Overview
The Admin Dashboard is a comprehensive management interface for administrators to monitor and manage the grievance resolution system.

---

## Creating Admin Users

### Method 1: Direct Database Insertion
To create an admin user directly in the MySQL database:

```sql
-- Connect to your MySQL database
USE students_db;

-- Insert admin user (password is BCrypt encrypted in the actual database)
INSERT INTO users (name, email, password, role, is_active, created_at, updated_at) 
VALUES (
  'Admin User',
  'admin@resolve.com',
  -- BCrypt hash of 'admin123' (example hash, use actual BCrypt hash)
  '$2a$10$slYQmyNdGzin7olVyJCL0OPST9/PgBkqquzi.Hy1yK1J0D0AI2afu',
  'ADMIN',
  true,
  NOW(),
  NOW()
);
```

### Method 2: Application Registration (To Be Implemented)
Update the registration endpoint to accept a registration key for admin creation.

---

## Admin Test Credentials

### Default Admin Account
- **Email:** `admin@resolve.com`
- **Password:** `admin123`
- **Role:** ADMIN

---

## Admin Dashboard Features

### 1. **Dashboard Home** 📊
- **System Status:** Real-time operational status
- **System Alerts:** Critical notifications and warnings
- **Key Metrics:**
  - Total Complaints
  - Completed Today
  - In Progress
  - Pending
  - Staff Present
  - Avg Resolution Time
- **Complaints by Category:** Visual breakdown
- **Quick Actions:** Fast access to common tasks

### 2. **Manage Complaints** 📄
- View all complaints in a detailed table
- Filter by status, category, priority
- Search functionality
- Assign complaints to staff
- Update complaint status
- Export complaint data
- Sort by date submitted

**Complaint Status Options:**
- ✅ Completed
- ⏳ In Progress
- ⏰ Pending

**Priority Levels:**
- 🔴 Urgent
- 🟠 High
- 🟢 Normal
- 🔵 Low

---

### 3. **Staff Management** 👥
- View all staff members and their status
- Monitor staff performance:
  - Complaints assigned
  - Complaints completed
  - Completion rate (%)
- Staff information cards showing:
  - Name & role
  - Active/On Leave status
  - Current workload
  - Performance metrics
- Quick actions:
  - Edit staff details
  - Assign new complaints
  - Suspend access (if needed)

**Staff Status Indicators:**
- 🟢 Active (Online/Available)
- 🟣 On Leave (Unavailable)
- 🔴 Offline (Not connected)

---

### 4. **Attendance & Leaves** 📅
- Daily attendance tracking
- Staff check-in/check-out times
- Leave request management
- Attendance statistics:
  - Present count
  - Absent count
  - On Leave count
  - Total staff
- Historical attendance records
- Leave approval interface

---

### 5. **Reports** 📈
Generate and view comprehensive reports:

**Daily Complaint Report:**
- Complaints filed today
- Resolved today
- Pending resolution
- Average resolution time

**Weekly Performance Report:**
- Total resolved complaints
- Avg resolution time
- Success rate %
- Trend analysis

**Staff Performance Report:**
- Top performing staff
- Avg complaints per person
- Total completed this week
- Individual staff analytics

**Customer Satisfaction Report:**
- Overall rating (1-5)
- Total feedback received
- Positive feedback %
- Improvement areas

---

### 6. **Analytics** 📊
Advanced analytics and visualizations:

**Complaint Trends:**
- Volume over time (line chart)
- Seasonal patterns
- Peak complaint hours

**Category Distribution:**
- Pie chart breakdown
- Category-wise resolution rates
- Most problematic categories

**Resolution Time Analytics:**
- Average time per category
- Trend over time
- Target vs actual

**Filter by Period:**
- This Month
- Last Month
- Last 3 Months
- Last Year

---

### 7. **System Alerts** 🔔
Real-time notifications about system events:

**Alert Types:**
- 🟠 **Warning:** High priority complaints pending, worker nearing limits
- 🔵 **Info:** System updates, schedule information
- 🟢 **Success:** Backup completed, system optimizations

**Alert Management:**
- View all system alerts
- Configure alert preferences
- Dismiss alerts
- Set up email notifications

---

### 8. **Settings** ⚙️

**System Configuration:**
- Maximum complaints per worker (default: 12)
- Target resolution time in hours
- Auto-assignment settings

**Security Settings:**
- Two-Factor Authentication toggle
- Audit logging enable/disable
- Admin activity logging

**Notification Preferences:**
- Email notifications
- SMS alerts for critical issues
- Daily digest options

**Danger Zone:**
- Clear system cache
- Reset data (use with caution!)
- Database backup

---

## Dashboard Metrics Explained

### Resolution Rate
```
(Completed Complaints / Total Complaints) × 100
```

### Average Resolution Time
Average hours taken to resolve complaints from filing to closure.

### Staff Efficiency
```
(Complaints Completed / Complaints Assigned) × 100
```

### System Health
Overall operational status based on:
- Database connectivity
- API response times
- Worker availability
- Complaint queue status

---

## Workflow Examples

### Scenario 1: Handling a High-Priority Complaint
1. Go to **Manage Complaints**
2. Filter by "Priority: High"
3. Find the urgent complaint
4. Click "Update" to assign to available staff
5. Monitor progress in **Track Status**
6. Check **Attendance** to ensure staff is available

### Scenario 2: Monitoring Daily Performance
1. Visit **Dashboard Home** for quick overview
2. Check **Staff Management** for workload distribution
3. Review **Attendance & Leaves** for today's coverage
4. Check **System Alerts** for any issues
5. Generate **Daily Complaint Report** for documentation

### Scenario 3: Analyzing Trends
1. Go to **Analytics**
2. Select desired time period
3. View complaint trends by category
4. Check resolution time patterns
5. Generate **Weekly Performance Report**
6. Identify improvement areas

---

## Best Practices

✅ **Do:**
- Check system alerts daily
- Monitor staff workload distribution
- Review reports weekly
- Update complaint statuses regularly
- Maintain accurate attendance records
- Back up data regularly

❌ **Don't:**
- Leave high-priority complaints unassigned
- Overload individual staff members
- Ignore system warnings
- Delete historical data without backup
- Disable security features
- Assign complaints to on-leave staff

---

## Keyboard Shortcuts

| Key | Action |
|-----|--------|
| `D` | Go to Dashboard |
| `C` | View Complaints |
| `W` | Staff Management |
| `A` | Attendance |
| `R` | Reports |
| `L` | Logout |

---

## Troubleshooting

**Issue:** Admin dashboard not loading
- **Solution:** Clear browser cache, verify admin role in database

**Issue:** Complaints not showing up
- **Solution:** Check database connection, refresh page

**Issue:** Attendance data not updating
- **Solution:** Ensure staff check-in/out feature is enabled

**Issue:** Charts not rendering
- **Solution:** Update browser, enable JavaScript

---

## Security Notes

🔒 **Important Security Practices:**
1. Use strong, unique admin passwords
2. Enable Two-Factor Authentication
3. Regularly review audit logs
4. Limit admin account access
5. Monitor suspicious activities
6. Update system regularly
7. Keep database backed up

---

## Support & Contact

For issues or questions about the Admin Dashboard:
- Contact: support@resolve.com
- Documentation: [Link to full docs]
- Report Issues: [Link to issue tracker]

---

## Version Information

- **Dashboard Version:** 1.0.0
- **Last Updated:** March 6, 2026
- **Compatible Backend:** Spring Boot 4.0+
- **Compatible Frontend:** React 18+

---
